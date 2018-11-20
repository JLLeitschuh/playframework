package com.lightbend.play

abstract class WellBehavedPluginTest extends AbstractIntegrationTest {

    def "plugin does not force creation of build dir during configuration"() {
        given:
        applyPlugin()

        when:
        build('tasks')

        then:
        !new File(projectDir, 'build').exists()
    }

    def "plugin can build with empty project"() {
        given:
        applyPlugin()

        expect:
        build(mainTask)
    }

    protected applyPlugin(File target = buildFile) {
        target << """
            plugins {
                id '${getPluginName()}'
            }
        """
    }

    String getMainTask() {
        'assemble'
    }

    abstract String getPluginName()
}
