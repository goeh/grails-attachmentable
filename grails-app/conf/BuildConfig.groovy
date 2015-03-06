grails.project.work.dir = "target"
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
    inherits("global") { }
    log "warn"
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        compile("org.apache.tika:tika-parsers:1.7") {
            exclude "xmlbeans"
            exclude "asm-debug-all"
        }
	compile 'org.compass-project:compass:2.2.0'
    }
    plugins {
        build ":tomcat:7.0.55"

        build(":release:3.0.1",
                ":rest-client-builder:1.0.3") {
            export = false
        }
        test(":hibernate4:4.3.6.1") {
            export = false
        }
    }
}
