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
package com.macrobit.grails.plugins.attachmentable.util

import com.macrobit.grails.plugins.attachmentable.domains.AttachmentLink
import com.macrobit.grails.plugins.attachmentable.domains.Attachment
import org.apache.commons.io.FileUtils

class AttachmentableUtil {

    static boolean isAttachmentable(def bean) {
        bean?.metaClass?.hasProperty(bean, 'attachments') != null
    }

    static void delete(File file) {
        if (file?.exists()) {
            if (file.isDirectory()) {
                FileUtils.deleteDirectory file
            } else {
                file.delete()
            }
        }
    }

    // fix

    static String fixClassName(Class clazz) {
        fixClassName(clazz.name)
    }

    static String fixClassName(String className) {
        // handle proxied class names
        int i = className.indexOf('_$$_javassist')
        if (i > -1) {
            className = className[0..i - 1]
        }
        className
    }

    // dir

    static File getDir(config, Attachment attachment, boolean createDirs = false) {
        getDir(config, attachment.lnk, createDirs)
    }

    static File getDir(config, AttachmentLink link, boolean createDirs = false) {
        getDir(config, link.referenceClass, link.referenceId, createDirs)
    }

    static File getDir(config,
                       reference,
                       boolean createDirs = false) {
        getDir(config, reference.getClass().name, reference.id, createDirs)
    }
    
    static File getDir(config,
                       String referenceClass,
                       Long referenceId,
                       boolean createDirs = false) {
        referenceClass = AttachmentableUtil.fixClassName(referenceClass)
        File uploadDir = new File(
                config.grails.attachmentable.uploadDir, referenceClass)
        uploadDir = new File(uploadDir, "$referenceId")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }
        uploadDir
    }

    // file

    static File getFile(config,
                        Attachment attachment,
                        boolean createDirs = false) {
        File uploadDir = getDir(config, attachment, createDirs)
        String filename = "${attachment.id}"
        if (attachment.ext) {
            filename += '.' + attachment.ext
        }
        File file = new File(uploadDir, filename)

        file
    }

}
