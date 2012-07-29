/* Copyright 2010 Mihai Cazacu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.macrobit.grails.plugins.attachmentable.taglibs

import com.macrobit.grails.plugins.attachmentable.util.AttachmentableUtil

class AttachmentsTagLib {

    static namespace = 'attachments'

    /* -------------------------------- TAGS -------------------------------- */

    def deleteLink = {attrs, body ->
        def attachment = attrs.remove('attachment')
        def label = body() ?: attrs.remove('label')
        def returnPageURI = attrs.remove('returnPageURI')
        attrs.controller = 'attachmentable'
        attrs.action = 'delete'
        attrs.id = attachment.id
        if (returnPageURI) {
            attrs.params = [returnPageURI: returnPageURI]
        }

        out << g.link(attrs) { label }
    }

    def downloadLink = {attrs, body ->
        def attachment = attrs.remove('attachment')
        def label = body() ?: attachment?.filename?.encodeAsHTML()

        attrs.controller = 'attachmentable'
        attrs.action = 'download'
        attrs.id = attachment.id
        def params = [:]
        if (booleanAttrValue(attrs, 'withContentType', false)) {
            params.withContentType = ''
        }
        if (booleanAttrValue(attrs, 'inline', false)) {
            params.inline = ''
        }
        if(params) {
            attrs.params = params
        }

        out << g.link(attrs) { label }
    }

    def each = {attrs, body ->
        def bean = attrs.remove('bean')
        def varName = attrs.remove('var') ?: 'attachment'
        def status = attrs.remove('status') ?: 'status'
         
        def inputName = attrs.remove('inputName')
        def inputNames = attrs.remove('inputNames') ?: []
        if (!inputNames && inputName) {
            inputNames = [inputName]
        }

        if (AttachmentableUtil.isAttachmentable(bean)) {
            def attachments
            if (inputNames) {
                attachments = bean.getAttachments(inputNames, [:])
            } else {
                attachments = bean.attachments
            }

            attachments?.eachWithIndex {attachment, idx ->
                out << body((varName): attachment, (status): idx)
            }
        }
    }

    def total = {attrs ->
        def bean = attrs.remove('bean')
        def inputName = attrs.remove('inputName')
        def inputNames = attrs.remove('inputNames') ?: []
        if (!inputNames && inputName) {
            inputNames = [inputName]
        }

        if (AttachmentableUtil.isAttachmentable(bean)) {
            out << bean.getTotalAttachments(inputNames)
        }
    }

    // FORM
    
    def style = {attrs ->
        out << g.render(
                template: '/attachmentable/resources/style',
                plugin: 'attachmentable')
    }

    def progressBar = {
        out << """<div id="progressbar" class="progressbar"></div>"""
    }

    def uploadForm = {attrs ->
        def bean = attrs.remove('bean')
        def inputName = attrs.remove('inputName') ?: 'attachment'
        def styleClass = attrs.remove('styleClass')

        if (AttachmentableUtil.isAttachmentable(bean)) {
            out << g.render(
                template: '/attachmentable/form',
                plugin: 'attachmentable',
                model: [attachmentable: bean,
                        inputName: inputName,
                        styleClass: styleClass])
        }
    }

    def script = {attrs ->
        def inputName = attrs.remove('inputName') ?: 'attachment'
        def importJS = booleanAttrValue(attrs, 'importJS')
        def updateInterval = intAttrValue(attrs, 'updateInterval') ?: 500
        def maxFiles = intAttrValue(attrs, 'maxFiles', -1)
        def updateElemId = attrs.remove('updateElemId')
        def redirect = attrs.remove('redirect')
        def allowedExt = attrs.remove('acceptExt') ?: []
        allowedExt = allowedExt.join('|')

        out << g.render(
                template: '/attachmentable/resources/script',
                plugin: 'attachmentable',
                model: [inputName: inputName,
                        importJS: importJS,
                        updateInterval: updateInterval,
                        maxFiles: maxFiles,
                        allowedExt: allowedExt,
                        updateElemId: updateElemId,
                        redirect: redirect])
    }

    // workaround for grails version < 1.2.RCX

    def resource = {attrs ->
        def dir = attrs.dir
        def file = attrs.file
        def usePluginDir = booleanAttrValue(attrs, 'usePluginDir', false) 

        dir = (dir.startsWith('/') ?  dir : "/$dir")
        def href = g.resource(
                dir: "${usePluginDir ? pluginContextPath : ''}$dir", 
                file: file)
        out << href
    }

    static final Map FILE_ICON_MAP = [
            pdf: 'page_white_acrobat',
            doc: 'page_white_word',
            docx: 'page_white_word',
            xls: 'page_white_excel',
            xlsx: 'page_white_excel',
            ppt: 'page_white_powerpoint',
            gif: 'page_white_picture',
            png: 'page_white_picture',
            jpg: 'page_white_picture',
            jpeg: 'page_white_picture',
            bmp: 'page_white_picture',
            tif: 'page_white_picture',
            mov: 'page_white_cd',
            wav: 'page_white_cd',
            mp3: 'page_white_cd',
            raw: 'page_white_camera',
            swf: 'page_white_flash',
            txt: 'page_white_text',
            zip: 'page_white_zip',
            xml: 'page_white_code',
            htm: 'page_white_code',
            html: 'page_white_code',
            groovy: 'page_white_code',
            php: 'page_white_php'
    ]

    def icon = {attrs ->
        def bean = attrs.attachment
        if(! bean) {
            throwTagError("Tag [icon] is missing required attribute [attachment]")
        }
        out << "<img width=\"16\" height=\"16\" src=\"" + g.resource(plugin:'attachmentable', dir: 'images/silk', file: FILE_ICON_MAP[bean.ext?.toLowerCase()] ?: 'page_white') + ".png\" alt=\"${bean.ext}\"/>"
    }

    /* ------------------------------- UTILS -------------------------------- */

    private int intAttrValue(Map attrs,
                             String attrName,
                             int defaultValue = 0) {
        def val = defaultValue
        if (attrs.containsKey(attrName)) {
            val = attrs.remove(attrName) as int
        }
        val
    }

    private boolean booleanAttrValue(Map attrs,
                                     String attrName,
                                     boolean defaultValue = true) {
        def val = defaultValue
        if (attrs.containsKey(attrName)) {
            val = Boolean.valueOf(attrs.remove(attrName))
        }
        val
    }

}
