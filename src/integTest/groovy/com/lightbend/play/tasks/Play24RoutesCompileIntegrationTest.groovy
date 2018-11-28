package com.lightbend.play.tasks

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Ignore

class Play24RoutesCompileIntegrationTest extends AbstractRoutesCompileIntegrationTest {

    @Override
    def getJavaRoutesFileName(String packageName, String namespace) {
        return "${namespace ? namespace + '/' :''}controllers/${packageName ? packageName + '/' :''}routes.java"
    }

    @Override
    def getReverseRoutesFileName(String packageName, String namespace) {
        return "${namespace ? namespace + '/' :''}controllers/${packageName ? packageName + '/' :''}ReverseRoutes.scala"
    }

    @Override
    def getScalaRoutesFileName(String packageName, String namespace) {
        return "${packageName?:'router'}/Routes.scala"
    }

    @Override
    def getOtherRoutesFileNames() {
        return [
                {packageName, namespace -> "${namespace ? namespace + '/' :''}controllers/${packageName ? packageName + '/' :''}javascript/JavaScriptReverseRoutes.scala" },
                {packageName, namespace -> "${packageName?:'router'}/RoutesPrefix.scala" }
        ]
    }

    def "can specify route compiler type as injected"() {
        given:
        withRoutesTemplate()
        withInjectedRoutesController()
        buildFile << """
play {
    platform {
        injectedRoutesGenerator.set(true)
    }
}
"""
        expect:
        build('compileScala')
        and:
        createRouteFileList().each {
            assert new File(destinationDir, it).isFile()
        }
    }

    def "recompiles when route compiler type is changed"() {
        when:
        withRoutesTemplate()
        then:
        build('compileScala')

        when:
        withInjectedRoutesController()
        buildFile << """
play {
    platform {
        injectedRoutesGenerator.set(true)
    }
}
"""
        then:
        BuildResult result = build('compileScala')
        result.task(ROUTES_COMPILE_TASK_PATH).outcome == TaskOutcome.SUCCESS
        and:
        createRouteFileList().each {
            assert new File(destinationDir, it).isFile()
        }
    }

    private withInjectedRoutesController() {
        file("app/controllers/Application.scala").with {
            // change Scala companion object into a regular class
            text = text.replaceFirst(/object/, "class")
        }
    }

    @Ignore
    def "failure to generate routes fails the build with useful message"() {
        given:
        File confDir = temporaryFolder.newFolder('conf')
        new File(confDir, "routes") << """
# This will cause route compilation failure since overload is not supported.
GET        /        com.foobar.HelloController.index()
GET        /*path   com.foobar.HelloController.index(path)
        """
        expect:
        BuildResult result = buildAndFail(ROUTES_COMPILE_TASK_PATH)
        result.output.contains("Using different overloaded methods is not allowed. If you are using a single method in combination with default parameters, make sure you declare them all explicitly.")
    }
}
