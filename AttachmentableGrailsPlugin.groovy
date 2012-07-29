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

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import com.macrobit.grails.plugins.attachmentable.core.ajax.AjaxMultipartResolver as AMR
import com.macrobit.grails.plugins.attachmentable.core.Attachmentable
import com.macrobit.grails.plugins.attachmentable.domains.Attachment
import com.macrobit.grails.plugins.attachmentable.core.exceptions.AttachmentableException
import com.macrobit.grails.plugins.attachmentable.util.AttachmentableUtil
import org.springframework.web.multipart.commons.CommonsMultipartFile
import com.macrobit.grails.plugins.attachmentable.services.AttachmentableService
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
import org.springframework.web.multipart.MultipartFile
import com.macrobit.grails.plugins.attachmentable.core.ajax.ProgressDescriptor
import org.apache.commons.logging.LogFactory
import com.macrobit.grails.plugins.attachmentable.core.ajax.AjaxMultipartResolver

class AttachmentableGrailsPlugin {

    static LOG = LogFactory.getLog('com.macrobit.grails.plugins.AttachmentableGrailsPlugin')

    def version = '0.3.0-SNAPSHOT'
    def grailsVersion = '1.1 > *'
    def dependsOn = [hibernate: '1.1 > *']
    def pluginExcludes = [
        'lib/**',

        'grails-app/conf/**',

        'grails-app/views/testEntry/**',
        'grails-app/views/error.gsp',

        'grails-app/controllers/com/macrobit/grails/plugins/attachmentable/controllers/test/**',

        'grails-app/domain/com/macrobit/grails/plugins/attachmentable/domains/test/**',

        'grails-app/web-app/META-INF',
        'grails-app/web-app/WEB-INF'
    ]
	def observe = ['controllers']
	def loadAfter = ['controllers']

    def author = 'Mihai Cazacu'
    def authorEmail = 'cazacugmihai@gmail.com'
    def title = 'Attachmentable Plugin'
    def description = 'A plugin that allows you to add attachments to domain classes in a generic manner.'

    def documentation = 'http://grails.org/plugin/attachmentable'

    def doWithSpring = {
        def config = application.config

        // multipartResolver bean
        multipartResolver(AjaxMultipartResolver) {
            def cfg = config.grails.attachmentable

            maxInMemorySize = cfg?.maxInMemorySize ? cfg.maxInMemorySize as int : 1024
            maxUploadSize = cfg?.maxUploadSize ? cfg.maxUploadSize as long : 1024000000

            if (cfg?.uploadTempDir) {
                uploadTempDir = cfg.uploadTempDir
            }
        }

        // poster evaluator
        def evaluator = config.grails.attachmentable?.poster?.evaluator
        if (!evaluator) {
            evaluator = { request.user }
            config.grails.attachmentable.poster.evaluator = evaluator
            LOG.debug "Attachmentable config(poster evaluator): 'request.user'"
        }

        attachmentFileConverter(com.macrobit.grails.plugins.attachmentable.compass.FileContentConverter) {
        }
    }

    def doWithDynamicMethods = {ctx ->
        AttachmentableService service = ctx.getBean('attachmentableService')
        def config = application.config

        // upload dir
        fixUploadDir application

        // enhance controllers
        application.controllerClasses?.each {c ->
            addControllerMethods config, c.clazz.metaClass, service
        }

        // enhance domain classes
        application.domainClasses?.each {d ->
            if (Attachmentable.class.isAssignableFrom(d.clazz) || getAttachmentableProperty(d)) {
                addDomainMethods config, d.clazz.metaClass, service
            }
        }
    }

    def onChange = {event ->
        def ctx = event.ctx

        if (event.source && ctx && event.application) {
            AttachmentableService service = ctx.getBean('attachmentableService')
            def config = application.config

            // upload dir
            if ('Config'.equals(event.source.name)) {
                fixUploadDir application
            }
            // enhance domain class
            else if (application.isDomainClass(event.source)) {
                def c = application.getDomainClass(event.source.name)
                if (Attachmentable.class.isAssignableFrom(c) || getAttachmentableProperty(c)) {
                    addDomainMethods config, c.metaClass, service
                }
            }
            // enhance controller
            else if (application.isControllerClass(event.source)) {
                def c = application.getControllerClass(event.source.name)
                addControllerMethods config, c.metaClass, service
            }
        }
    }

    /* ------------------------------- UTILS -------------------------------- */
    public static final String ATTACHMENTABLE_PROPERTY_NAME = "attachmentable";

    private getAttachmentableProperty(domainClass) {
        GrailsClassUtils.getStaticPropertyValue(domainClass.clazz, ATTACHMENTABLE_PROPERTY_NAME)
    }

    private void fixUploadDir(application) {
        def dir = application.config.grails.attachmentable?.uploadDir
        if (!dir) {
            String userHome  = System.properties.'user.home'
            String appName   = application.metadata['app.name']
            dir = new File(userHome, appName).canonicalPath
            application.config.grails.attachmentable.uploadDir = dir
        }
        LOG.debug "Attachmentable config(upload dir): '$dir'"
    }

    private void addControllerMethods(def config,
                                      MetaClass mc,
                                      AttachmentableService service) {
        mc.uploadStatus = {
            def controllerInstance = delegate
            def request = controllerInstance.request

            ProgressDescriptor pd = request.session[AMR.progressAttrName(request)]
            controllerInstance.render(pd ?: '')
        }

        mc.attachUploadedFilesTo = {reference, List inputNames = [] ->
            def controllerInstance = delegate
            def request = controllerInstance.request

            if (AttachmentableUtil.isAttachmentable(reference)) {
                // user
                def evaluator = config.grails.attachmentable.poster.evaluator
                def user = null

                if (evaluator instanceof Closure) {
                    evaluator.delegate = controllerInstance
                    evaluator.resolveStrategy = Closure.DELEGATE_ONLY
                    user = evaluator.call()
                }

                if (!user) {
                    throw new AttachmentableException(
                        "No [grails.attachmentable.poster.evaluator] setting defined or the evaluator doesn't evaluate to an entity or string. Please define the evaluator correctly in grails-app/conf/Config.groovy or ensure attachmenting is secured via your security rules.")
                }

                if (!(user instanceof String) && !user.id) {
                    throw new AttachmentableException(
                        "The evaluated Attachment poster is not a persistent instance.")
                }

                // files
                List<MultipartFile> filesToUpload = []
                List<MultipartFile> uploadedFiles = []

                if (request instanceof DefaultMultipartHttpServletRequest) {
                    request.multipartFiles.each {k, v ->
                        if (!inputNames || inputNames.contains(k)) {
                            if (v instanceof List) {
                                v.each {MultipartFile file ->
                                    filesToUpload << file
                                }
                            } else {
                                filesToUpload << v
                            }
                        }
                    }

                    // upload
                    uploadedFiles = service.upload(user, reference, filesToUpload)
                }

                // result
                [filesToUpload: filesToUpload, uploadedFiles: uploadedFiles]
            }
        }

        mc.existAttachments = {
            def controllerInstance = delegate
            def request = controllerInstance.request
            def result = false

            if (request instanceof DefaultMultipartHttpServletRequest) {
                request.multipartFiles.each {k, v ->
                    if (v instanceof List) {
                        v.each {MultipartFile file ->
                            if (file.size)
                                result = true
                        }
                    } else {
                        if (v.size)
                            result = true
                    }
                }
            }

            return result
        }
    }

    private void addDomainMethods(def config,
                                  MetaClass mc,
                                  AttachmentableService service) {
        // add

        mc.addAttachment = {def poster,
                            CommonsMultipartFile file ->
            service.addAttachment(config, poster, delegate, file)
        }

        // get

        mc.getAttachments = {String inputName, params = [:] ->
            getAttachments(inputName ? [inputName] : [], params)
        }

        mc.getAttachments = {List inputNames = [], params = [:] ->
            service.findAttachmentsByReference(delegate, inputNames, params)
        }

        // count

        mc.getTotalAttachments = {String inputName ->
            getTotalAttachments(inputName ? [inputName] : [])
        }

        mc.getTotalAttachments = {List inputNames = [] ->
            service.countAttachmentsByReference(delegate, inputNames)
        }

        // remove

        mc.removeAttachments = {->
            service.removeAttachments(delegate)
        }

        mc.removeAttachments = {List inputNames ->
            service.removeAttachments(delegate, inputNames)
        }

        mc.removeAttachment = {Attachment attachment ->
            service.removeAttachment(attachment)
        }

        mc.removeAttachment = {Long attachmentId ->
            service.removeAttachment(attachmentId)
        }

        mc.removeAttachment = {String inputName ->
            removeAttachments([inputName])
        }

    }

}
