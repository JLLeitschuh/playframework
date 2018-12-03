package com.lightbend.play.application.basic

import com.lightbend.play.application.PlayTestApplicationIntegrationTest
import com.lightbend.play.fixtures.app.BasicPlayApp
import com.lightbend.play.fixtures.app.PlayApp
import com.lightbend.play.fixtures.test.TestExecutionResult

class PlayTestBasicAppIntegrationTest extends PlayTestApplicationIntegrationTest {

    @Override
    PlayApp getPlayApp() {
        new BasicPlayApp(versionNumber)
    }

    @Override
    void verifyTestOutput(TestExecutionResult result) {
        result.assertTestClassesExecuted("ApplicationSpec", "IntegrationSpec")
        result.testClass("ApplicationSpec").assertTestCount(2, 0, 0)
        result.testClass("IntegrationSpec").assertTestCount(1, 0, 0)
    }
}