package com.playframework.gradle.application.multiproject

import com.playframework.gradle.application.PlayIdeaPluginIntegrationTest
import com.playframework.gradle.fixtures.app.PlayApp
import com.playframework.gradle.fixtures.app.PlayMultiProject
import org.gradle.play.internal.platform.PlayMajorVersion

import static com.playframework.gradle.application.basic.PlayIdeaPluginBasicIntegrationTest.PLAY_VERSION_TO_CLASSPATH_SIZE
import static com.playframework.gradle.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME

class PlayIdeaPluginMultiprojectIntegrationTest extends PlayIdeaPluginIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new PlayMultiProject(playVersion)
    }

    File getModuleFile() {
        file("primary/primary.iml")
    }

    @Override
    List<File> getIdeFiles() {
        super.getIdeFiles() + [file("${playApp.name}.iml"), file('submodule/submodule.iml'), file('javalibrary/javalibrary.iml')]
    }

    String[] getSourcePaths() {
        [
            "public",
            "conf",
            "app",
            "build/src/play/routes"
        ]
    }

    String[] getBuildTasks() {
        [
            ":ideaModule",
            ":ideaProject",
            ":ideaWorkspace",
            ":idea",
            ":javalibrary:ideaModule",
            ":javalibrary:idea",
            ":primary:$ROUTES_COMPILE_TASK_NAME".toString(),
            ":primary:ideaModule",
            ":primary:idea",
            ":submodule:ideaModule",
            ":submodule:idea"
        ]
    }

    int getExpectedScalaClasspathSize() {
        PLAY_VERSION_TO_CLASSPATH_SIZE[PlayMajorVersion.forPlayVersion(playVersion.toString())]
    }
}
