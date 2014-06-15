grails.project.work.dir = "target"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6

grails.project.dependency.resolution = {
    inherits("global") { }
    log "warn"
    repositories {        
        grailsCentral()
        mavenCentral()
    }
    dependencies {
        compile("org.apache.tika:tika-parsers:1.5") {
            exclude "xmlbeans"
            exclude "asm-debug-all"
        }
	compile 'org.compass-project:compass:2.2.0'
    }
    plugins {
        build(":tomcat:$grailsVersion",
                ":release:2.2.1",
                ":rest-client-builder:1.0.3") {
            export = false
        }
        runtime(":hibernate:$grailsVersion") { export = false }
    }
}
