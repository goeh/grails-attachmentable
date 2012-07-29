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
package com.macrobit.grails.plugins.attachmentable.core.ajax

import javax.servlet.http.HttpServletRequest
import org.apache.commons.fileupload.FileUploadBase
import org.apache.commons.fileupload.FileUploadException
import org.apache.commons.fileupload.ProgressListener
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.multipart.commons.CommonsFileUploadSupport.MultipartParsingResult

class AjaxMultipartResolver extends CommonsMultipartResolver {

    public final static String PROGRESS_PREFIX = "AjaxMultipartResolver:"

    static String progressAttrName(final HttpServletRequest req) {
        // PROGRESS_PREFIX + req.requestURI
        PROGRESS_PREFIX
    }

    private void removeProgressDescriptor(final HttpServletRequest req) {
        req.session.removeAttribute(progressAttrName(req))
    }

    private void updateProgressDescriptor(final HttpServletRequest req,
                                          final long bytesRead,
                                          final long bytesTotal) {
        ProgressDescriptor pd = req.session[progressAttrName(req)]
        if (!pd) {
            pd = new ProgressDescriptor()
            req.session[progressAttrName(req)] = pd
        }
        pd.bytesRead = bytesRead
        pd.bytesTotal = bytesTotal
    }

    @Override
    protected MultipartParsingResult parseRequest(final HttpServletRequest req) {
        updateProgressDescriptor req, 0, 0

        String encoding = determineEncoding(req)
        ServletFileUpload fileUpload = prepareFileUpload(encoding)

        fileUpload.progressListener = [update: {
            long pBytesRead, long pContentLength, int pItems ->
            updateProgressDescriptor req, pBytesRead, pContentLength
        }] as ProgressListener

        MultipartParsingResult result = null
        try {
            result = parseFileItems(fileUpload.parseRequest(req), encoding)
        } catch (FileUploadBase.SizeLimitExceededException e) {
            throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), e)
        } catch (FileUploadException e) {
            throw new MultipartException(
                'Could not parse multipart servlet request', e)
        }

        removeProgressDescriptor req
        result
    }

}
