<g:if test="${importJS}">
    
    <script type="text/javascript" src="${attachments.resource(dir: 'js/jquery', file: 'jquery.js')}"></script>
    <script type="text/javascript" src="${attachments.resource(dir: 'js/jquery-ui', file: 'jquery.ui.core.js')}"></script>
    <script type="text/javascript" src="${attachments.resource(dir: 'js/jquery-ui', file: 'jquery.ui.widget.js')}"></script>
    <script type="text/javascript" src="${attachments.resource(dir: 'js/jquery-ui', file: 'jquery.ui.progressbar.js')}"></script>
    <script type="text/javascript" src="${attachments.resource(dir: 'js/jquery-multifile', file: 'jquery.form.js')}"></script>
    <script type="text/javascript" src="${attachments.resource(dir: 'js/jquery-multifile', file: 'jquery.MultiFile.js')}"></script>

</g:if>

<g:javascript>
    jQuery.noConflict();
    jQuery(document).ready(function() {
        jQuery('#${inputName}').MultiFile({
            max: ${maxFiles},
            accept: '${allowedExt}',
            STRING: {
                remove:'<g:message code="atachmentable.form.label.remove" default="x"/>',
                denied:'<g:message code="atachmentable.form.alert.denied" default="You cannot select a $ext file.\\nTry again..."/>',
                file:'<g:message code="atachmentable.form.label.file" default="$file"/>',
                selected:'<g:message code="atachmentable.form.alert.file" default="File selected: $file"/>',
                duplicate:'<g:message code="atachmentable.form.alert.duplicate" default="This file has already been selected:\\n$file"/>'
            }
        });

        var time;
        var progressbar = jQuery('#progressbar');
        var form = jQuery('#uploadForm').ajaxForm({
            beforeSubmit: function() {
                progressbar.progressbar({ value: 0 });
                progressbar.show();

                var url = '${g.createLink(controller: 'attachmentable', action: 'uploadInfo')}';

                time = setInterval(function() {
                     jQuery.get(
                        url + '?timestamp=' + new Date().getTime(),
                        function(data) {
                            if (!data) return;
                            data = data.split("/");

                            var progressVal = Math.round(data[0] / data[1] * 100);
                            progressbar.progressbar({ value: progressVal });
                     });
                }, ${updateInterval});
            },
            success: function(responseText, statusText) {
                jQuery('input:file').MultiFile('reset')
                clearInterval(time);
                progressbar.progressbar({ value: 100 });
                progressbar.hide();
                <g:if test="${updateElemId}">
                    if (statusText == 'success') {
                        jQuery('#${updateElemId}').html(responseText)
                    }
                </g:if>
                <g:if test="${redirect}">
                    window.location = "${redirect}";
                    window.location.reload(true);
                </g:if>
            }
        });
    });
</g:javascript>