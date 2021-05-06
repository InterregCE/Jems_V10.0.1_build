package io.cloudflight.jems.server.project.service.application.execute_pre_condition_check

import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ExecutePreConditionCheckTest : UnitTest() {

    private val projectId = 1L

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @RelaxedMockK
    lateinit var preConditionCheckPlugin: PreConditionCheckPlugin

    @InjectMockKs
    lateinit var executePreConditionCheck: ExecutePreConditionCheck

    @Test
    fun `should execute pre condition check plugin for the project application`() {
        val pluginExpectedResult = PreConditionCheckResult(listOf(), false)
        every {
            jemsPluginRegistry.get(PreConditionCheckPlugin::class, "standard-pre-condition-check-plugin")
        } returns preConditionCheckPlugin
        every { preConditionCheckPlugin.check(projectId) } returns pluginExpectedResult
        val preConditionActualResult = executePreConditionCheck.execute(projectId)

        assertThat(preConditionActualResult).isEqualTo(pluginExpectedResult)

    }

}
