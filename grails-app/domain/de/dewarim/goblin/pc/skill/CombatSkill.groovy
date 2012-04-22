package de.dewarim.goblin.pc.skill

import de.dewarim.goblin.Dice

/**
 * A CombatSkill which grants the owner a set of bonuses, once for each level.
 */
class CombatSkill extends Skill{

    /*
     * TODO: replace with a comprehensive skill system,
     * which also checks dependencies (for example, equipped weapons)
     * and may change CombatAttributes as well as offer
     * skills which can be activated for special attacks.
     *
     * And a pony, too.
     */

    static constraints = {
        strike nullable:true
        initiative nullable:true
        parry nullable: true
        damage nullable:true
    }
    
    Dice strike
    Dice initiative
    Dice parry
    Dice damage

}
