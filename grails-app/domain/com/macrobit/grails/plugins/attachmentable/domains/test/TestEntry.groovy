package com.macrobit.grails.plugins.attachmentable.domains.test

import com.macrobit.grails.plugins.attachmentable.core.Attachmentable
import com.macrobit.grails.plugins.attachmentable.domains.Attachment

class TestEntry implements Attachmentable {

    String title

    String toString() { "$title" }

    def onAddAttachment = {Attachment attachment ->
        println attachment.inputName + ' ' + attachment.filename
    }

}
