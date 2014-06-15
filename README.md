# Attachmentable Plugin

This plugin provides a generic way to add and manage attachments for a given application.
It is based on 2 JQuery plugins:

* [Multiple file upload](http://www.fyneworks.com/jquery/multiple-file-upload) 
* [Progressbar](http://docs.jquery.com/UI/Progressbar)

## Requirements

* Grails Version: 1.2.1 and above
* JDK: 1.5 and above

## Installation

Add a plugin dependency in BuildConfig.groovy

    compile ":attachmentable:0.3.0"

## Configuration

### The default one looks like:

    grails.attachmentable.maxInMemorySize = 1024
    grails.attachmentable.maxUploadSize = 1024000000
    grails.attachmentable.uploadDir = YOUR_USER_HOME/APP_NAME

### You need to define a poster evaluator in grails-app/conf/Config.groovy. The default one looks like:

    grails.attachmentable.poster.evaluator = { request.user }

But if you store users in the session instead you may want this to be:

    grails.attachmentable.poster.evaluator = { session.user }

Or, if you use the [Acegi plugin|http://grails.org/plugin/acegi]:

    grails.attachmentable.poster.evaluator = { getAuthUserDomain() }

Or in case of the [Spring Security Core Plugin|http://grails.org/plugin/spring-security-core]:

    grails.attachmentable.poster.evaluator = { getPrincipal() }

In version 0.2.0 and above poster can be a String:

    grails.attachmentable.poster.evaluator = { "unknown" }

### Searchable plugin

You can let the [searchable](http://www.grails.org/plugin/searchable) plugin index you attachments using
the [Apache Tika](http://tika.apache.org/) parser.
Install the searchable plugin and add this to your Config.groovy:

    grails.attachmentable.searchableFileConverter = "attachmentFileConverter"

You can then search for text inside your attachments in PDF, Word, Excel and many other document formats.
*Note* that the current implementation will only index the first 2 MB of text, to conserve memory.

## Usage

You have two options.

Implement the *Attachmentable* interface:

    import com.macrobit.grails.plugins.attachmentable.core.Attachmentable

    class Topic implements Attachmentable {
    }

*or*

Add a static property

    class Topic {
        static attachmentable = true
    }

Add some attachments:

GSP:

    <input type="file" name="pictures"/>
    <input type="file" name="pictures"/>
    <input type="file" name="pictures"/>
    ...
    <input type="file" name="movies"/>
    <input type="file" name="movies"/>
    ...
    <input type="file" name="someLabel"/>
    ...

or

    <attachments:style />
    <attachments:uploadForm bean="${topicInstance}" />
    <attachments:script updateInterval="100"/>

Controller:

    attachUploadedFilesTo(topicInstance)

Query:

    def topic = Topic.get(1)
    
    // get
    
    def attachments = topic.attachments
    def pictures = topic.getAttachments('pictures')
    
    // count
    
    def attachmentsNo = topic.totalAttachments
    def picturesNo = topic.getTotalAttachments('pictures')
    def mediaNo = topic.getTotalAttachments(['pictures', 'movies'])
    
    // remove
    
    topic.removeAttachments()
    topic.removeAttachments(['pictures', 'movies'])
    
    topic.removeAttachment(attachmentInstance)
    topic.removeAttachment(attachmentId)
    topic.removeAttachment('pictures')

To remove all attachments when the "owner" domain is deleted, add this to your domain:

    transient def beforeDelete = {
        withNewSession{
            removeAttachments()
        }
    }

It's important to wrap the method call in withNewSession { ... } See [http://jira.codehaus.org/browse/GRAILSPLUGINS-2386]

In a GSP:

    <attachments:each bean="${topicInstance}">
        <attachments:icon attachment="${attachment}"/>
        <attachments:deleteLink
                             attachment="${attachment}"
                             label="${'[X]'}"
                             returnPageURI="${createLink(action: 'actionName', id: topicInstance.id)}"/>
        <attachments:downloadLink
                             attachment="${attachment}"/>
        ${attachment.niceLength}
    </attachments:each>

To add some logic to handle newly created attachments, just add an "onAddAttachment()" method to your Attachmentable domain class:

    class Topic implements Attachmentable {

        def onAddAttachment = {attachment ->
            // post processing logic for newly added attachment
        }
    }

## Internationalization

- check the "messages.properties" file from the plugin directory.

## Tags

**deleteLink**

Attribute      | Description
-------------- | --------------------
attachment     | The attachment instance.
label          | The link label (also you can use the body tag instead of this attribute).
returnPageURI  | The page URI to return after the action.

**downloadLink**

Attribute      | Description
-------------- | --------------------
attachment     | The attachment instance.
inline         | true if attachment should be viewed inline in the browser.

**each**

Attribute      | Description
-------------- | --------------------
bean           | The attachmentable instance.
inputName      | The input name.
inputNames     | The list of input names.
var            |  The name of the item (default: 'attachment').
status         | The name of a variable to store the iteration index in. For the first iteration this variable has a value of 0, for the next, 1, and so on.

**total**

Attribute      | Description
-------------- | --------------------
bean           | The attachmentable instance.
inputName      | The input name.
inputNames     | The list of input names.

**style**

Attribute      | Description
-------------- | --------------------

**script**

Attribute      | Description
-------------- | --------------------
inputName      | The file input name (default: 'attachment').
importJS       | Boolean value: insert or not the JS files.
updateInterval | The update interval(in milliseconds) for the progress bar.
maxFiles       | The maximum files allowed.
acceptExt      | Accepted extensions.
updateElemId   | The ID of the element that will be updated with the response data.
redirect       | URL to redirect to when file upload is finished.

**uploadForm**

Attribute      | Description
-------------- | --------------------
bean           | The attachmentable instance.
inputName      | The file input name (default: 'attachment').
styleClass     | The style class.

**progressBar**

Attribute      | Description
-------------- | --------------------

**icon**

Generates an <img> tag with an appropriate icon based on file type, using a subset of the [Silk Icons](http://www.famfamfam.com/lab/icons/silk/).

Attribute      | Description
-------------- | --------------------
attachment     | The Attachment instance

## Version History

* 0.3.0 (October 5, 2012) - Updated dependencies and uploaded source to github.
* 0.2.1 (January 14, 2011) - Fixed problem with spring security plugin.
* 0.2.0 (January 5, 2011) - Optional searchable indexing of files. Icons.
* 0.1.1-SNAPSHOT (June 4, 2010) - Some small fixes/enhancements.
* 0.1-SNAPSHOT (March 17, 2010) - First release.

# Suggestions, comments or bugs

Feel free to submit questions or comments to the [Google Group - Grails Dev Discuss](https://groups.google.com/forum/#!forum/grails-dev-discuss)
Please report any issues to [https://github.com/goeh/grails-attachmentable/issues]
