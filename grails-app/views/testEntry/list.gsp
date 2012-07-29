
<%@ page import="com.macrobit.grails.plugins.attachmentable.domains.test.TestEntry" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'testEntry.label', default: 'TestEntry')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'testEntry.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="title" title="${message(code: 'testEntry.title.label', default: 'Title')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${testEntryInstanceList}" status="i" var="testEntryInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${testEntryInstance.id}">${fieldValue(bean: testEntryInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: testEntryInstance, field: "title")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${testEntryInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
