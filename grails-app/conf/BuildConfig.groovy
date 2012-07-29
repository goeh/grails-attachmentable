grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits( "global" ) {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {        
        grailsPlugins()
        grailsHome()
        grailsCentral()
        
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {

        runtime 'org.apache.pdfbox:fontbox:1.1.0',
                'org.apache.pdfbox:pdfbox:1.1.0',
                'org.apache.pdfbox:jempbox:1.1.0'

	compile 'org.compass-project:compass:2.1.0'
        compile 'org.apache.tika:tika-core:0.7'
        compile('org.apache.tika:tika-parsers:0.7') {
            excludes "xercesImpl", "xmlParserAPIs", "xml-apis", "log4j"
        }
    }
}
