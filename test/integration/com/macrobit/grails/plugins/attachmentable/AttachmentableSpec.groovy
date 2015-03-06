package com.macrobit.grails.plugins.attachmentable

import com.macrobit.grails.plugins.attachmentable.domains.test.TestEntry
import com.macrobit.grails.plugins.attachmentable.domains.test.TestPoster
import org.springframework.mock.web.MockMultipartFile

/**
 * Test spec.
 */
class AttachmentableSpec extends grails.test.spock.IntegrationSpec {

    def attachmentableService

    def "add attachment to domain instance"() {
        given:
        def file = new MockMultipartFile("file", "/tmp/test1.txt", "text/plain", "This is a test".getBytes())
        def reference = new TestEntry(title: "Attach something").save(failOnError: true)
        def other = new TestEntry(title: "No player").save(failOnError: true)
        def poster = new TestPoster(name: "Test").save(failOnError: true)

        when:
        attachmentableService.addAttachment(poster, reference, file)

        then:
        attachmentableService.countAttachmentsByReference(reference) == 1
        attachmentableService.countAttachmentsByPoster(poster) == 1

        when:
        def result = attachmentableService.findAttachmentsByReference(reference)

        then:
        result.size() == 1

        when:
        attachmentableService.removeAttachments(reference)

        then:
        attachmentableService.countAttachmentsByReference(reference) == 0
    }
}
