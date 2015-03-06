package com.macrobit.grails.plugins.attachmentable.controllers.test

import com.macrobit.grails.plugins.attachmentable.domains.test.TestEntry
import com.macrobit.grails.plugins.attachmentable.domains.test.TestPoster

class TestEntryController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        if (!TestEntry.count()) {
            TestEntry.findAll()*.delete()
            TestPoster.findAll()*.delete(flush: true)
            10.times {
                new TestEntry(title: "entry$it").save()
                new TestPoster(name: "user$it").save(flush: true)
            }
        }

        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [testEntryInstanceList: TestEntry.list(params), testEntryInstanceTotal: TestEntry.count()]
    }

    def create() {
        def testEntryInstance = new TestEntry()
        testEntryInstance.properties = params
        return [testEntryInstance: testEntryInstance]
    }

    def save() {
        def testEntryInstance = new TestEntry(params)
        if (testEntryInstance.save(flush: true)) {
            def result = attachUploadedFilesTo(testEntryInstance)
            flash.message = "${message(code: 'default.created.message', args: [message(code: 'testEntry.label', default: 'TestEntry'), testEntryInstance.id])}"
            redirect(action: "show", id: testEntryInstance.id)
        }
        else {
            render(view: "create", model: [testEntryInstance: testEntryInstance])
        }
    }

    def show() {
        def testEntryInstance = TestEntry.get(params.id)

        if (!testEntryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'testEntry.label', default: 'TestEntry'), params.id])}"
            redirect(action: "list")
        }
        else {
            [testEntryInstance: testEntryInstance]
        }
    }

    def edit() {
        def testEntryInstance = TestEntry.get(params.id as long)
        if (!testEntryInstance) {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'testEntry.label', default: 'TestEntry'), params.id])}"
            redirect(action: "list")
        }
        else {
            return [testEntryInstance: testEntryInstance]
        }
    }

    def update() {
        def testEntryInstance = TestEntry.get(params.id)
        if (testEntryInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (testEntryInstance.version > version) {

                    testEntryInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'testEntry.label', default: 'TestEntry')] as Object[], "Another user has updated this TestEntry while you were editing")
                    render(view: "edit", model: [testEntryInstance: testEntryInstance])
                    return
                }
            }
            testEntryInstance.properties = params
            if (!testEntryInstance.hasErrors() && testEntryInstance.save(flush: true)) {
                flash.message = "${message(code: 'default.updated.message', args: [message(code: 'testEntry.label', default: 'TestEntry'), testEntryInstance.id])}"
                redirect(action: "show", id: testEntryInstance.id)
            }
            else {
                render(view: "edit", model: [testEntryInstance: testEntryInstance])
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'testEntry.label', default: 'TestEntry'), params.id])}"
            redirect(action: "list")
        }
    }

    def delete() {
        def testEntryInstance = TestEntry.get(params.id)
        if (testEntryInstance) {
            try {
                testEntryInstance.delete(flush: true)
                flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'testEntry.label', default: 'TestEntry'), params.id])}"
                redirect(action: "list")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'testEntry.label', default: 'TestEntry'), params.id])}"
                redirect(action: "show", id: params.id)
            }
        }
        else {
            flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'testEntry.label', default: 'TestEntry'), params.id])}"
            redirect(action: "list")
        }
    }

}
