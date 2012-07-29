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
package com.macrobit.grails.plugins.attachmentable.domains

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import com.macrobit.grails.plugins.attachmentable.util.AttachmentableUtil

class Attachment {

    // file
    String name
    String ext
    String contentType
    Long length
    Date dateCreated

    // poster
    String posterClass
    Long posterId

    // input name
    String inputName

    static belongsTo = [lnk: AttachmentLink]

    static constraints = {
        name nullable: false, blank: false
        ext nullable: true, blank: true
        contentType nullable: true, blank: true
        length min: 0L

        posterClass blank: false
        posterId min: 0L
    }
    static transients = ['filename', 'path', 'niceLength', 'poster']
    static searchable = {
        only = ['name', 'ext', 'path']
        path converter: CH.config.grails.attachmentable.searchableFileConverter ?: 'string'
    }

    static mapping = {
        cache true
    }

    String toString() {
        filename
    }

    /* ------------------------------- UTILS -------------------------------- */

    def getFilename() {
        ext ? "$name.$ext" : "$name"
    }

    String getNiceLength() {
        if(length >= 10485760) { // 10 MB
            return "${(length / 1048576).intValue()} MB"
        } else if(length >= 1024) { // 1 kB
            return "${(length / 1024).intValue()} kB"
        }
        "$length"
    }

    String getPath() {
        AttachmentableUtil.getFile(CH.config, this).absolutePath
    }

    def getPoster() {
        posterId == 0L ? posterClass : getClass().classLoader.loadClass(posterClass).get(posterId)
    }

}