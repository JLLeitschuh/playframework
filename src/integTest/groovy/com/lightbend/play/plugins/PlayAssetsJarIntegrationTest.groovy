package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest
import com.lightbend.play.fixtures.app.BasicPlayApp
import com.lightbend.play.fixtures.archive.JarTestFixture
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import static com.lightbend.play.plugins.PlayApplicationPlugin.ASSETS_JAR_TASK_NAME
import static com.lightbend.play.plugins.PlayApplicationPlugin.JAR_TASK_NAME

class PlayAssetsJarIntegrationTest extends AbstractIntegrationTest {

    private static final JAR_TASK_PATH = ":$JAR_TASK_NAME".toString()
    private static final ASSETS_JAR_TASK_PATH = ":$ASSETS_JAR_TASK_NAME".toString()

    def setup() {
        new BasicPlayApp().writeSources(projectDir)
        settingsFile << "rootProject.name = 'play-app'"
    }

    def "can bundle assets in JAR file"() {
        when:
        BuildResult result = build('createAssetsJar')

        then:
        //result.task(JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        result.task(ASSETS_JAR_TASK_PATH).outcome == TaskOutcome.SUCCESS
        file('build/libs/play-app-assets.jar').isFile()
        jar('build/libs/play-app-assets.jar').containsDescendants(
                'public/images/favicon.svg',
                'public/stylesheets/main.css',
                'public/javascripts/hello.js')
    }

    JarTestFixture jar(String fileName) {
        new JarTestFixture(file(fileName))
    }
}
