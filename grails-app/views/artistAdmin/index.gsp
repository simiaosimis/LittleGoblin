<!DOCTYPE HTML>
<html>
<head>

	<meta name="layout" content="main"/>
    <g:render template="/shared/header"/>	
	
</head>
<body class="main">
<g:render template="/shared/logo"/>
    <div class="navigation">
        <g:link controller="portal" action="landing"><g:message code="link.to.home"/></g:link>
        <g:link controller="admin" action="index"><g:message code="link.to.admin"/> </g:link>
    </div>
<div class="colmask ">

	<div class="full_col2">

		<h1><g:message code="artist.admin.headline" /></h1>
		<div id="message" class="message"><g:if test="${flash.message}">${flash.message}</g:if>
		</div>
		<div class="intro">
			<g:message code="artist.admin.intro"/>
		</div>
		<div class="artist_create type_create">
			<g:render template="/artistAdmin/create" />
		</div>
		<h2><g:message code="artist.admin.list"/> </h2>
		<div class="artist_list type_list" id="artistList">
			<g:render template="/artistAdmin/list" model="[artists:artists]"/>
		</div>
	</div>

</div>
<g:render template="/shared/footer"/>
</body>
</html>