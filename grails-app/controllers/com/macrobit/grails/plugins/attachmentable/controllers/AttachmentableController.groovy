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
package com.macrobit.grails.plugins.attachmentable.controllers

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

import com.macrobit.grails.plugins.attachmentable.domains.Attachment
import com.macrobit.grails.plugins.attachmentable.domains.AttachmentLink
import com.macrobit.grails.plugins.attachmentable.util.AttachmentableUtil
import grails.orm.PagedResultList
import javax.servlet.http.HttpServletResponse

class AttachmentableController {

    def attachmentableService

    // download

    def download = {
        Attachment attachment = Attachment.get(params.id as Long)

        if (attachment) {
            File file = AttachmentableUtil.getFile(CH.config, attachment)

            if (file.exists()) {
                String filename = attachment.filename /*.replaceAll(/\s/, '_')*/

                ['Content-disposition': "${params.containsKey('inline') ? 'inline' : 'attachment'};filename=\"$filename\"",
                    'Cache-Control': 'private',
                    'Pragma': ''].each {k, v ->
                    response.setHeader(k, v)
                }

                if (params.containsKey('withContentType')) {
                    response.contentType = attachment.contentType
                } else {
                    response.contentType = 'application/octet-stream'
                }
                file.withInputStream{fis->
                    response.outputStream << fis
                }

                // response.contentLength = file.length()
                // response.outputStream << file.readBytes()
                // response.outputStream.flush()
                // response.outputStream.close()
                return
            }
        }

        response.status = HttpServletResponse.SC_NOT_FOUND
    }

    def show = {
        // Default show action is to display the attachment inline in the browser.
        if (!params.containsKey('inline')) {
            params.inline = ''
        }
        if (!params.containsKey('withContentType')) {
            params.withContentType = ''
        }
        forward(action:'download', params:params)
    }

    // upload

    def upload = {
        AttachmentLink lnk = new AttachmentLink(params.attachmentLink)

        attachUploadedFilesTo(lnk.reference)

        render 'success'
    }

    def uploadInfo = {
        uploadStatus()
    }

    // delete

    def delete = {
        def result = attachmentableService.removeAttachment(params.id as Long)

        if (params.returnPageURI) {
            redirect url: params.returnPageURI - request.contextPath
        } else {
            render (result > 0 ? 'success' : 'failed')
        }
    }

}
