
<%@ page import="com.macrobit.grails.plugins.attachmentable.domains.test.TestEntry" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <attachments:style />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'testEntry.label', default: 'TestEntry')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${testEntryInstance}">
            <div class="errors">
                <g:renderErrors bean="${testEntryInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${testEntryInstance?.id}" />
                <g:hiddenField name="version" value="${testEntryInstance?.version}" />
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
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
                                   
        <attachments:uploadForm
                bean="${testEntryInstance}"
                styleClass="uploadFormContainer"/>
        <attachments:script updateInterval="100"/>
%{--
                            maxFiles="2"
                            acceptExt="['gif', 'wmv', 'jpg', 'zip', 'gz']"/>
--}%
    </body>
</html>
