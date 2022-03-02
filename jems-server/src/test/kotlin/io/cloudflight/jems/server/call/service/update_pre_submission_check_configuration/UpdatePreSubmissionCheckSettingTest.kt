package io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration

import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.callDetail
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.pre_submission_check.PreSubmissionCheckBlocked
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdatePreSubmissionCheckSettingsTest : UnitTest() {

    private val pluginKey = "standard-pre-submission-check"
    @MockK
    lateinit var persistence: CallPersistence

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @InjectMockKs
    lateinit var updatePreSubmissionCheckSettings: UpdatePreSubmissionCheckSettings

    @Test
    fun `should update pre-submission check plugin settings when there is no problem`(){
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKey ) } returns PreSubmissionCheckBlocked()
        every { persistence.updateProjectCallPreSubmissionCheckPlugin(1L, PreSubmissionPlugins(
            pluginKey = pluginKey,
            firstStepPluginKey = pluginKey
        ))
        } returns callDetail(id = 1L, preSubmissionCheckPluginKey = pluginKey, firstStepPreSubmissionCheckPluginKey = pluginKey)
        assertThat(updatePreSubmissionCheckSettings.update(1L,
            PreSubmissionPlugins(pluginKey = pluginKey,
                firstStepPluginKey = pluginKey
            ))).isEqualTo(callDetail(id = 1L, preSubmissionCheckPluginKey = pluginKey, firstStepPreSubmissionCheckPluginKey = pluginKey))
    }

    @Test
    fun `should throw Exception when plugin with provided key is not valid`(){
        val expectedException = RuntimeException("expected exception")
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKey ) } throws expectedException
        assertThrows<RuntimeException> {  (updatePreSubmissionCheckSettings.update(1L, PreSubmissionPlugins(pluginKey = pluginKey))) }
    }
}
