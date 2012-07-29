
<%@ page import="com.macrobit.grails.plugins.attachmentable.domains.test.TestEntry" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'testEntry.label', default: 'TestEntry')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${testEntryInstance}">
            <div class="errors">
                <g:renderErrors bean="${testEntryInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:uploadForm action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="title"><g:message code="testEntry.title.label" default="Title" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: testEntryInstance, field: 'title', 'errors')}">
                                    <g:textField name="title" value="${testEntryInstance?.title}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="title">File1</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: testEntryInstance, field: 'title', 'errors')}">
                                    <input type="file" name="attachment"/>
                                </td>
                            </tr>

%{--
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="title">File1</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: testEntryInstance, field: 'title', 'errors')}">
                                    <input type="file" name="attachment"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="title">File2</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: testEntryInstance, field: 'title', 'errors')}">
                                    <input type="file" name="attachment1"/>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="title">File2</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: testEntryInstance, field: 'title', 'errors')}">
                                    <input type="file" name="attachment2"/>
                                </td>
                            </tr>
--}%

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:uploadForm>
        </div>
    </body>
</html>
