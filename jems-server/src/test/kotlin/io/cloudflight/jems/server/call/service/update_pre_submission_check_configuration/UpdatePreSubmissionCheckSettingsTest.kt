package io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration

import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.PreConditionCheckSamplePlugin
import io.cloudflight.jems.server.plugin.PreConditionCheckSamplePluginKey
import io.cloudflight.jems.server.plugin.pre_submission_check.PreSubmissionCheckOff
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UpdatePreSubmissionCheckSettingsTest : UnitTest() {

    companion object {
        private fun call(is2step: Boolean): CallDetail {
            val call = mockk<CallDetail>()
            every { call.is2StepCall() } returns is2step
            return call
        }
    }

    @MockK
    lateinit var persistence: CallPersistence

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @InjectMockKs
    lateinit var updatePreSubmissionCheckSettings: UpdatePreSubmissionCheckSettings

    @BeforeEach
    fun reset() {
        clearMocks(persistence)
        clearMocks(jemsPluginRegistry)
    }

    @Test
    fun `should update pre-submission check plugin settings when 2step call and both plugins provided`(){
        val call = call(true)
        every { persistence.getCallById(1L) } returns call

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "jems-pre-condition-check-off") } returns PreSubmissionCheckOff()
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, PreConditionCheckSamplePluginKey) } returns PreConditionCheckSamplePlugin()

        every { persistence.updateProjectCallPreSubmissionCheckPlugin(1L, any()) } returns call

        assertThat(updatePreSubmissionCheckSettings.update(
            callId = 1L,
            pluginKeys = PreSubmissionPlugins(
                pluginKey = "jems-pre-condition-check-off",
                firstStepPluginKey = PreConditionCheckSamplePluginKey,
            )
        )).isEqualTo(call)

        verify(exactly = 1) { persistence.updateProjectCallPreSubmissionCheckPlugin(
            callId = 1L,
            pluginKeys = PreSubmissionPlugins("jems-pre-condition-check-off", PreConditionCheckSamplePluginKey),
        ) }
    }

    @Test
    fun `should not update pre-submission check plugin settings when 2step call and only first plugin provided`(){
        val call = call(true)
        every { persistence.getCallById(8L) } returns call

        val emptyPlugin = mockk<PreConditionCheckPlugin>()
        every { emptyPlugin.getKey() } returns ""

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "missing") } returns emptyPlugin
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, PreConditionCheckSamplePluginKey) } returns PreConditionCheckSamplePlugin()

        assertThat(updatePreSubmissionCheckSettings.update(
            callId = 8L,
            pluginKeys = PreSubmissionPlugins(
                pluginKey = "missing",
                firstStepPluginKey = PreConditionCheckSamplePluginKey,
            )
        )).isEqualTo(call)

        verify(exactly = 0) { persistence.updateProjectCallPreSubmissionCheckPlugin(any(), any()) }
    }

    @Test
    fun `should not update pre-submission check plugin settings when 2step call and only second plugin provided`(){
        val call = call(true)
        every { persistence.getCallById(15L) } returns call

        val emptyPlugin = mockk<PreConditionCheckPlugin>()
        every { emptyPlugin.getKey() } returns ""

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "missing") } returns emptyPlugin
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, PreConditionCheckSamplePluginKey) } returns PreConditionCheckSamplePlugin()

        assertThat(updatePreSubmissionCheckSettings.update(
            callId = 15L,
            pluginKeys = PreSubmissionPlugins(
                pluginKey = PreConditionCheckSamplePluginKey,
                firstStepPluginKey = "missing",
            )
        )).isEqualTo(call)

        verify(exactly = 0) { persistence.updateProjectCallPreSubmissionCheckPlugin(any(), any()) }
    }

    @Test
    fun `should not update pre-submission check plugin settings when 2step call and no any plugin provided`(){
        val call = call(true)
        every { persistence.getCallById(20L) } returns call

        val emptyPlugin = mockk<PreConditionCheckPlugin>()
        every { emptyPlugin.getKey() } returns ""

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "missing") } returns emptyPlugin

        assertThat(updatePreSubmissionCheckSettings.update(
            callId = 20L,
            pluginKeys = PreSubmissionPlugins(
                pluginKey = "missing",
                firstStepPluginKey = "missing",
            )
        )).isEqualTo(call)

        verify(exactly = 0) { persistence.updateProjectCallPreSubmissionCheckPlugin(any(), any()) }
    }

    @Test
    fun `should update pre-submission check plugin settings when 1step call and plugin provided`(){
        val call = call(false)
        every { persistence.getCallById(17L) } returns call

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, PreConditionCheckSamplePluginKey) } returns PreConditionCheckSamplePlugin()

        every { persistence.updateProjectCallPreSubmissionCheckPlugin(17L, any()) } returns call

        assertThat(updatePreSubmissionCheckSettings.update(
            callId = 17L,
            pluginKeys = PreSubmissionPlugins(
                pluginKey = PreConditionCheckSamplePluginKey,
                firstStepPluginKey = null,
            )
        )).isEqualTo(call)

        verify(exactly = 1) { persistence.updateProjectCallPreSubmissionCheckPlugin(17L, PreSubmissionPlugins(PreConditionCheckSamplePluginKey, null)) }
    }

    @Test
    fun `should not update pre-submission check plugin settings when 1step call and plugin not provided`(){
        val call = call(false)
        every { persistence.getCallById(24L) } returns call

        val emptyPlugin = mockk<PreConditionCheckPlugin>()
        every { emptyPlugin.getKey() } returns ""
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "missing") } returns emptyPlugin

        every { persistence.updateProjectCallPreSubmissionCheckPlugin(24L, any()) } returns call

        assertThat(updatePreSubmissionCheckSettings.update(
            callId = 24L,
            pluginKeys = PreSubmissionPlugins(
                pluginKey = "missing",
            )
        )).isEqualTo(call)

        verify(exactly = 0) { persistence.updateProjectCallPreSubmissionCheckPlugin(any(), any()) }
    }

}
