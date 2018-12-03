package com.lightbend.play.plugins

import com.lightbend.play.AbstractIntegrationTest

import static com.lightbend.play.fixtures.file.FileFixtures.findFile
import static com.lightbend.play.fixtures.Repositories.playRepositories
import static com.lightbend.play.plugins.PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME

class PlayJavaScriptPluginIntegrationTest extends AbstractIntegrationTest {

    def setup() {
        buildFile << """
            plugins {
                id 'com.lightbend.play-application'
                id 'com.lightbend.play-javascript'
            }
            
            ${playRepositories()}
        """
    }

    def "can minify JavaScript files"() {
        given:
        File assetsDir = temporaryFolder.newFolder('app', 'assets')
        new File(assetsDir, 'test.js') << 'test'

        when:
        build(JS_MINIFY_TASK_NAME)

        then:
        File outputDir = file('build/src/play/javaScript')
        outputDir.isDirectory()
        File[] jsFles = outputDir.listFiles()
        jsFles.length == 2
        findFile(jsFles, 'test.js')
        findFile(jsFles, 'test.min.js')
    }

    def "can add source directories to default source set"() {
        given:
        File assetsDir = temporaryFolder.newFolder('app', 'assets')
        new File(assetsDir, 'test.js') << 'test'
        File extraJavascriptDir = temporaryFolder.newFolder('extra', 'javascript')
        new File(extraJavascriptDir, 'extra.js') << 'extra'

        buildFile << """
            sourceSets {
                main {
                    javaScript {
                        srcDir 'extra/javascript'
                    }
                }
            }
        """

        when:
        build(JS_MINIFY_TASK_NAME)

        then:
        File outputDir = file('build/src/play/javaScript')
        outputDir.isDirectory()
        File[] jsFles = outputDir.listFiles()
        jsFles.length == 4
        findFile(jsFles, 'test.js')
        findFile(jsFles, 'test.min.js')
        findFile(jsFles, 'extra.js')
        findFile(jsFles, 'extra.min.js')
    }
}
