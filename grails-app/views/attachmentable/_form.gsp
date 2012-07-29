<div <g:if test="${styleClass}">class="${styleClass}"</g:if>>
<g:form
    name="uploadForm"
    controller="attachmentable"
    action="upload"
    method="post"
    enctype="multipart/form-data">

    <attachments:progressBar/>

    <g:hiddenField name="attachmentLink.referenceClass" value="${attachmentable.class.name}"/>
    <g:hiddenField name="attachmentLink.referenceId" value="${attachmentable.id}"/>
    <input type="file" id="${inputName}" name="${inputName}"/>

    <g:submitButton name="${g.message(code:'attachment.upload.button.name', 'default': 'Upload')}"/>
</g:form>
</div>