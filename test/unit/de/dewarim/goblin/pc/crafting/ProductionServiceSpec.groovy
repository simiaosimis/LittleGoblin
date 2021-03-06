package de.dewarim.goblin.pc.crafting

import de.dewarim.goblin.ComponentType
import de.dewarim.goblin.ProductionService
import de.dewarim.goblin.UserAccount
import de.dewarim.goblin.item.Item
import de.dewarim.goblin.item.ItemService
import de.dewarim.goblin.item.ItemType
import de.dewarim.goblin.pc.PlayerCharacter
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import spock.lang.Shared
import spock.lang.Specification

/**
 * Unit test for ProductionService.
 */
@TestFor(ProductionService)
@Mock([PlayerCharacter, Item, ItemType, Component, Product, UserAccount,
        ProductCategory, ProductionJob, ProductionResource])
class ProductionServiceSpec extends Specification {

    def productionService = new ProductionService()
    def itemService = new ItemService()

    def user = new UserAccount(username: "crafty", passwd: 'foodFood', userRealName: 'none')
    def playerCharacter = new PlayerCharacter(name: "Crafter-1", user: user)
    def prodInputType = new ItemType(name: "gold nugget")
    def prodOutputType = new ItemType(name: "golden crown")
    def inputItems = new Item(prodInputType, playerCharacter)
    def prodCat = new ProductCategory(name: "head.wear")
    def crownProduct = new Product(name: "Crown Product", timeNeeded: 1, category: prodCat)
    def inputComponent = new Component(type: ComponentType.INPUT, itemType: prodInputType,
            product: crownProduct, amount: 2)
    def outputComponent = new Component(type: ComponentType.OUTPUT, itemType: prodOutputType,
            product: crownProduct, amount: 1)

    void setup() {
        productionService.itemService = itemService
    }

    void "test calculation of maximum products"() {
        given:
        prodInputType.save()
        inputItems.amount = 10
        saveDomainObjects()

        when:
        def allItems = Item.findAll()
        def items = playerCharacter.items
        def maxProduction = productionService.computeMaxProduction(crownProduct, playerCharacter)

        then:
        allItems.size() == 1
        items.size() == 1
        maxProduction == 5
    }

    void "test fetch ItemMap"() {
        given:
        inputItems.amount = 10
        inputItems.owner = playerCharacter
        saveDomainObjects()

        when:
        def itemMap = productionService.fetchItemMap(crownProduct, playerCharacter)

        then:
        itemMap != null
        itemMap.size() == 1
        itemMap.get(inputComponent).contains(inputItems)
        itemMap.get(inputComponent).size() == 1
    }

    void "extract item list from params"() {
        given:
        inputItems.amount = 10
        inputItems.owner = playerCharacter
        saveDomainObjects()
        def params = ["item_${inputItems.id}": '5', "item_0": '100', "items_-1": '-1']

        when:
        def items = productionService.extractItemListFromParams(params)

        then:
        items.size() == 1
        items.contains(inputItems)
    }

    void "fetch itemCountMap from params"() {
        given:
        inputItems.amount = 10
        inputItems.owner = playerCharacter
        saveDomainObjects()
        def params = ["item_${inputItems.id}": '5', "item_0": '100', "items_-1": '-1']

        when:
        def itemTypeToAmountMap = productionService.fetchItemCountMapFromParams(params)

        then:
        itemTypeToAmountMap.size() == 1
        itemTypeToAmountMap.get(prodInputType) == 5 // returns selected amount, not actual
    }

    void "enough resources selected"() {
        given:
        inputItems.amount = 10
        inputItems.owner = playerCharacter
        saveDomainObjects()
        def validSelection = ["item_${inputItems.id}": '5']
        def invalidSelection = ["item_${inputItems.id}": '1']

        when:
        def hasEnough = productionService.enoughResourcesSelected(crownProduct, playerCharacter, validSelection)
        def notEnough = productionService.enoughResourcesSelected(crownProduct, playerCharacter, invalidSelection)

        then:
        hasEnough
        !notEnough
    }

    void "create new production job"() {
        given:
        inputItems.amount = 10
        inputItems.owner = playerCharacter

        saveDomainObjects()
        def enough = ["item_${inputItems.id}": '5']
        def missing = ["item_${inputItems.id}": '1']

        when:
        def valid = productionService.createNewProductionJob(crownProduct, playerCharacter, enough)
        def invalid = productionService.createNewProductionJob(crownProduct, playerCharacter, missing)

        then:
        valid.result.isPresent()
        valid.result.get() != null
        valid.result.get() != Optional.empty()
        !invalid.result.present
        invalid.errors.find { it.equals('production.missing.resources') }
    }

    void "detect new production job with stolen items"() {
        given:
        inputItems.amount = 10
        def thief = new PlayerCharacter(name: "Crafter-2", user: user)
        inputItems.owner = thief

        saveDomainObjects()
        def enough = ["item_${inputItems.id}": '5']
        def missing = ["item_${inputItems.id}": '1']

        when:
        def validButStolen = productionService.createNewProductionJob(crownProduct, playerCharacter, enough)
        def invalidAndStolen = productionService.createNewProductionJob(crownProduct, playerCharacter, missing)

        then:
        !validButStolen.result.present
        !invalidAndStolen.result.present

        invalidAndStolen.errors.find { it.equals('production.missing.resources') }
        validButStolen.errors.find { it.equals('production.foreign.item') }
    }

    void "make products"() {
        given:
        inputItems.amount = 10
        inputItems.owner = playerCharacter
        saveDomainObjects()
        
        def enough = ["item_${inputItems.id}": '5']

        when:
        def optionalJob = productionService.createNewProductionJob(crownProduct, playerCharacter, enough)
        def productCount = productionService.makeProducts([optionalJob.result.get()])

        then:
        productCount == 1
        Component.findByItemType(prodOutputType)
        playerCharacter.items.find {
            it.type.equals(prodOutputType) && it.type.name.equals("golden crown")
        }
        ProductionResource.count() == 0
        Item.count() == 2
        playerCharacter.items.find {
            it.type.equals(prodInputType) && it.amount.equals(8)
        }
    }

    private void saveDomainObjects() {
        playerCharacter.save()
        prodInputType.save()
        inputItems.save()
        prodCat.save()
        crownProduct.save()
        prodOutputType.save()
        inputComponent.save()
        outputComponent.save()
    }
}
