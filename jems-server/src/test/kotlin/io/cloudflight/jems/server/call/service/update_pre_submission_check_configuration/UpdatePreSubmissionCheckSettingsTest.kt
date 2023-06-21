package io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportPartnerCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportPartnerCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportProjectCheckPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins
import io.cloudflight.jems.server.plugin.*
import io.cloudflight.jems.server.plugin.pre_submission_check.PreSubmissionCheckOff
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher

internal class UpdatePreSubmissionCheckSettingsTest : UnitTest() {

    companion object {
        private fun call(is2step: Boolean): CallDetail {
            val call = mockk<CallDetail>()
            every { call.is2StepCall() } returns is2step
            every { call.preSubmissionCheckPluginKey } returns null
            every { call.firstStepPreSubmissionCheckPluginKey } returns null
            every { call.reportPartnerCheckPluginKey } returns null
            every { call.reportProjectCheckPluginKey } returns null
            every { call.controlReportPartnerCheckPluginKey } returns null
            every { call.controlReportSamplingCheckPluginKey } returns null
            return call
        }
    }

    @MockK
    lateinit var persistence: CallPersistence

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var updatePreSubmissionCheckSettings: UpdatePreSubmissionCheckSettings

    @BeforeEach
    fun reset() {
        clearMocks(persistence, jemsPluginRegistry, auditPublisher)
    }

    @Test
    fun `should update pre-submission check plugin settings when 2step call and both plugins provided`() {
        val call = call(true)
        every { persistence.getCallById(1L) } returns call

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "jems-pre-condition-check-off") } returns PreSubmissionCheckOff()
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, PreConditionCheckSamplePluginKey) } returns PreConditionCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportPartnerCheckPlugin::class, PartnerReportCheckPluginKey) } returns ReportPartnerCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportProjectCheckPlugin::class, ProjectReportCheckPluginKey) } returns ReportProjectCheckSamplePlugin()
        every { jemsPluginRegistry.get(ControlReportPartnerCheckPlugin::class, PartnerControlReportCheckPluginKey) } returns PartnerControlReportCheckSamplePlugin()

        every { persistence.updateProjectCallPreSubmissionCheckPlugin(1L, any()) } returns call
        every { call.isPublished() } returns true
        every { call.id } returns 1L
        every { call.name } returns "Test"
        every { call.firstStepPreSubmissionCheckPluginKey } returns "no-check"
        every { call.preSubmissionCheckPluginKey } returns "no-check"
        every { call.reportPartnerCheckPluginKey } returns "no-check"
        every { call.reportProjectCheckPluginKey } returns "no-check"
        every { call.controlReportPartnerCheckPluginKey } returns "no-check"
        every { call.controlReportSamplingCheckPluginKey } returns "no-check"

        assertThat(
            updatePreSubmissionCheckSettings.update(
                callId = 1L,
                newPluginsConfig = PreSubmissionPlugins(
                    pluginKey = "jems-pre-condition-check-off",
                    firstStepPluginKey = PreConditionCheckSamplePluginKey,
                    reportPartnerCheckPluginKey = PartnerReportCheckPluginKey,
                    reportProjectCheckPluginKey = ProjectReportCheckPluginKey,
                    controlReportPartnerCheckPluginKey = PartnerControlReportCheckPluginKey,
                    controlReportSamplingCheckPluginKey = ControlReportSamplingCheckPluginKey,
                )
            )
        ).isEqualTo(call)

        verify(exactly = 1) {
            persistence.updateProjectCallPreSubmissionCheckPlugin(
                callId = 1L,
                pluginKeys = PreSubmissionPlugins(
                    pluginKey ="jems-pre-condition-check-off",
                    firstStepPluginKey = PreConditionCheckSamplePluginKey,
                    reportPartnerCheckPluginKey = PartnerReportCheckPluginKey,
                    reportProjectCheckPluginKey = ProjectReportCheckPluginKey,
                    controlReportPartnerCheckPluginKey = PartnerControlReportCheckPluginKey,
                    controlReportSamplingCheckPluginKey =ControlReportSamplingCheckPluginKey
                ),
            )
        }

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CALL_CONFIGURATION_CHANGED,
                entityRelatedId = 1L,
                description = "Configuration of published call id=1 name='Test' changed: Plugin selection was changed\n" +
                        "PreSubmissionCheckFirstStep changed from 'no-check' to 'key-1',\n" +
                        "PreSubmissionCheck changed from 'no-check' to 'jems-pre-condition-check-off',\n" +
                        "PreSubmissionCheckPartnerReport changed from 'no-check' to 'key-3',\n" +
                        "PreSubmissionCheckProjectReport changed from 'no-check' to 'key-5',\n" +
                        "PreSubmissionCheckPartnerControlReport changed from 'no-check' to 'key-6',\n" +
                        "PreSubmissionCheckControlReportSampling changed from 'no-check' to 'key-4'"
            )
        )
    }

    @Test
    fun `should not update pre-submission check plugin settings when 2step call and only first plugin provided`() {
        val call = call(true)
        every { persistence.getCallById(8L) } returns call

        val emptyPlugin = mockk<PreConditionCheckPlugin>()
        every { emptyPlugin.getKey() } returns ""

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "missing") } returns emptyPlugin
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, PreConditionCheckSamplePluginKey) } returns PreConditionCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportPartnerCheckPlugin::class, PartnerReportCheckPluginKey) } returns ReportPartnerCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportProjectCheckPlugin::class, ProjectReportCheckPluginKey) } returns ReportProjectCheckSamplePlugin()
        every { jemsPluginRegistry.get(ControlReportPartnerCheckPlugin::class, PartnerControlReportCheckPluginKey) } returns PartnerControlReportCheckSamplePlugin()

        assertThat(
            updatePreSubmissionCheckSettings.update(
                callId = 8L,
                newPluginsConfig = PreSubmissionPlugins(
                    pluginKey = "missing",
                    firstStepPluginKey = PreConditionCheckSamplePluginKey,
                    reportPartnerCheckPluginKey = PartnerReportCheckPluginKey,
                    reportProjectCheckPluginKey = ProjectReportCheckPluginKey,
                    controlReportPartnerCheckPluginKey = PartnerControlReportCheckPluginKey,
                    controlReportSamplingCheckPluginKey = ControlReportSamplingCheckPluginKey
                )
            )
        ).isEqualTo(call)

        verify(exactly = 0) { persistence.updateProjectCallPreSubmissionCheckPlugin(any(), any()) }
    }

    @Test
    fun `should not update pre-submission check plugin settings when 2step call and only second plugin provided`() {
        val call = call(true)
        every { persistence.getCallById(15L) } returns call

        val emptyPlugin = mockk<PreConditionCheckPlugin>()
        every { emptyPlugin.getKey() } returns ""

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "missing") } returns emptyPlugin
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, PreConditionCheckSamplePluginKey) } returns PreConditionCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportPartnerCheckPlugin::class, PartnerReportCheckPluginKey) } returns ReportPartnerCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportProjectCheckPlugin::class, ProjectReportCheckPluginKey) } returns ReportProjectCheckSamplePlugin()
        every { jemsPluginRegistry.get(ControlReportPartnerCheckPlugin::class, PartnerControlReportCheckPluginKey) } returns PartnerControlReportCheckSamplePlugin()

        assertThat(
            updatePreSubmissionCheckSettings.update(
                callId = 15L,
                newPluginsConfig = PreSubmissionPlugins(
                    pluginKey = PreConditionCheckSamplePluginKey,
                    firstStepPluginKey = "missing",
                    reportPartnerCheckPluginKey = PartnerReportCheckPluginKey,
                    reportProjectCheckPluginKey = ProjectReportCheckPluginKey,
                    controlReportPartnerCheckPluginKey = PartnerControlReportCheckPluginKey,
                    controlReportSamplingCheckPluginKey = ControlReportSamplingCheckPluginKey
                )
            )
        ).isEqualTo(call)

        verify(exactly = 0) { persistence.updateProjectCallPreSubmissionCheckPlugin(any(), any()) }
    }

    @Test
    fun `should not update pre-submission check plugin settings when 2step call and no any plugin provided`() {
        val call = call(true)
        every { persistence.getCallById(20L) } returns call

        val emptyPlugin = mockk<PreConditionCheckPlugin>()
        every { emptyPlugin.getKey() } returns ""
        val emptyReportPlugin = mockk<ReportPartnerCheckPlugin>()
        every { emptyReportPlugin.getKey() } returns ""
        val emptyProjectPlugin = mockk<ReportProjectCheckPlugin>()
        every { emptyProjectPlugin.getKey() } returns ""

        val emptyControlReportPlugin = mockk<ControlReportPartnerCheckPlugin>()
        every { emptyControlReportPlugin.getKey() } returns ""

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "missing") } returns emptyPlugin
        every { jemsPluginRegistry.get(ReportPartnerCheckPlugin::class, "missing") } returns emptyReportPlugin
        every { jemsPluginRegistry.get(ReportProjectCheckPlugin::class, "missing") } returns emptyProjectPlugin
        every { jemsPluginRegistry.get(ControlReportPartnerCheckPlugin::class, "missing") } returns emptyControlReportPlugin

        assertThat(
            updatePreSubmissionCheckSettings.update(
                callId = 20L,
                newPluginsConfig = PreSubmissionPlugins(
                    pluginKey = "missing",
                    firstStepPluginKey = "missing",
                    reportPartnerCheckPluginKey = "missing",
                    reportProjectCheckPluginKey = "missing",
                    controlReportPartnerCheckPluginKey = "missing",
                    controlReportSamplingCheckPluginKey = "missing"
                )
            )
        ).isEqualTo(call)

        verify(exactly = 0) { persistence.updateProjectCallPreSubmissionCheckPlugin(any(), any()) }
    }

    @Test
    fun `should update pre-submission check plugin settings when 1step call and plugin provided`() {
        val call = call(false)
        every { persistence.getCallById(17L) } returns call

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, PreConditionCheckSamplePluginKey) } returns PreConditionCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportPartnerCheckPlugin::class, PartnerReportCheckPluginKey) } returns ReportPartnerCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportProjectCheckPlugin::class, ProjectReportCheckPluginKey) } returns ReportProjectCheckSamplePlugin()
        every { jemsPluginRegistry.get(ControlReportPartnerCheckPlugin::class, PartnerControlReportCheckPluginKey) } returns PartnerControlReportCheckSamplePlugin()

        every { persistence.updateProjectCallPreSubmissionCheckPlugin(17L, any()) } returns call
        every { call.isPublished() } returns false
        every { call.id } returns 17L
        every { call.name } returns "Test2"
        every { call.firstStepPreSubmissionCheckPluginKey } returns null
        every { call.preSubmissionCheckPluginKey } returns "no-check"
        every { call.reportPartnerCheckPluginKey } returns "blocked"
        every { call.reportProjectCheckPluginKey } returns "blocked"
        every { call.controlReportPartnerCheckPluginKey } returns "no-check"
        every { call.controlReportSamplingCheckPluginKey } returns "no-check"

        assertThat(
            updatePreSubmissionCheckSettings.update(
                callId = 17L,
                newPluginsConfig = PreSubmissionPlugins(
                    pluginKey = PreConditionCheckSamplePluginKey,
                    firstStepPluginKey = "",
                    reportPartnerCheckPluginKey = PartnerReportCheckPluginKey,
                    reportProjectCheckPluginKey = ProjectReportCheckPluginKey,
                    controlReportPartnerCheckPluginKey = PartnerControlReportCheckPluginKey,
                    controlReportSamplingCheckPluginKey = ControlReportSamplingCheckPluginKey
                )
            )
        ).isEqualTo(call)

        verify(exactly = 1) {
            persistence.updateProjectCallPreSubmissionCheckPlugin(
                17L,
                PreSubmissionPlugins(
                    pluginKey = PreConditionCheckSamplePluginKey,
                    firstStepPluginKey = "",
                    reportPartnerCheckPluginKey = PartnerReportCheckPluginKey,
                    reportProjectCheckPluginKey = ProjectReportCheckPluginKey,
                    controlReportPartnerCheckPluginKey = PartnerControlReportCheckPluginKey,
                    controlReportSamplingCheckPluginKey = ControlReportSamplingCheckPluginKey
                )
            )
        }

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CALL_CONFIGURATION_CHANGED,
                entityRelatedId = 17L,
                description = "Configuration of not-published call id=17 name='Test2' changed: Plugin selection was changed\n" +
                        "PreSubmissionCheck changed from 'no-check' to 'key-1',\n" +
                        "PreSubmissionCheckPartnerReport changed from 'blocked' to 'key-3',\n" +
                        "PreSubmissionCheckProjectReport changed from 'blocked' to 'key-5',\n" +
                        "PreSubmissionCheckPartnerControlReport changed from 'no-check' to 'key-6',\n" +
                        "PreSubmissionCheckControlReportSampling changed from 'no-check' to 'key-4'"
            )
        )
    }

    @Test
    fun `should not update pre-submission check plugin settings when 1step call and plugin not provided`() {
        val call = call(false)
        every { persistence.getCallById(24L) } returns call

        val emptyPlugin = mockk<PreConditionCheckPlugin>()
        every { emptyPlugin.getKey() } returns ""
        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, "missing") } returns emptyPlugin
        every { jemsPluginRegistry.get(ReportPartnerCheckPlugin::class, PartnerReportCheckPluginKey) } returns ReportPartnerCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportProjectCheckPlugin::class, ProjectReportCheckPluginKey) } returns ReportProjectCheckSamplePlugin()
        every { jemsPluginRegistry.get(ControlReportPartnerCheckPlugin::class, PartnerControlReportCheckPluginKey) } returns PartnerControlReportCheckSamplePlugin()

        every { persistence.updateProjectCallPreSubmissionCheckPlugin(24L, any()) } returns call

        assertThat(
            updatePreSubmissionCheckSettings.update(
                callId = 24L,
                newPluginsConfig = PreSubmissionPlugins(
                    pluginKey = "missing",
                    reportPartnerCheckPluginKey = PartnerReportCheckPluginKey,
                    reportProjectCheckPluginKey = ProjectReportCheckPluginKey,
                    controlReportPartnerCheckPluginKey = PartnerControlReportCheckPluginKey,
                    controlReportSamplingCheckPluginKey = ControlReportSamplingCheckPluginKey
                )
            )
        ).isEqualTo(call)

        verify(exactly = 0) { persistence.updateProjectCallPreSubmissionCheckPlugin(any(), any()) }
    }

    @Test
    fun `should not update pre-submission check plugin settings when report check plugin not provided`() {
        val call = call(false)
        every { persistence.getCallById(25L) } returns call

        val emptyPlugin = mockk<PreConditionCheckPlugin>()
        every { emptyPlugin.getKey() } returns ""
        val emptyReportPlugin = mockk<ReportPartnerCheckPlugin>()
        every { emptyReportPlugin.getKey() } returns ""
        val emptyProjectPlugin = mockk<ReportProjectCheckPlugin>()
        every { emptyProjectPlugin.getKey() } returns ""
        val emptyControlReportPlugin = mockk<ControlReportPartnerCheckPlugin>()
        every { emptyControlReportPlugin.getKey() } returns ""

        every { jemsPluginRegistry.get(PreConditionCheckPlugin::class, PreConditionCheckSamplePluginKey) } returns PreConditionCheckSamplePlugin()
        every { jemsPluginRegistry.get(ReportPartnerCheckPlugin::class, "missing") } returns emptyReportPlugin
        every { jemsPluginRegistry.get(ReportProjectCheckPlugin::class, "missing") } returns emptyProjectPlugin
        every { jemsPluginRegistry.get(ControlReportPartnerCheckPlugin::class, "missing") } returns emptyControlReportPlugin


        every { persistence.updateProjectCallPreSubmissionCheckPlugin(25L, any()) } returns call

        assertThat(
            updatePreSubmissionCheckSettings.update(
                callId = 25L,
                newPluginsConfig = PreSubmissionPlugins(
                    pluginKey = PreConditionCheckSamplePluginKey,
                    reportPartnerCheckPluginKey = "missing",
                    reportProjectCheckPluginKey = "missing",
                    controlReportPartnerCheckPluginKey = PartnerControlReportCheckPluginKey,
                    controlReportSamplingCheckPluginKey = ControlReportSamplingCheckPluginKey
                )
            )
        ).isEqualTo(call)

        verify(exactly = 0) { persistence.updateProjectCallPreSubmissionCheckPlugin(any(), any()) }
    }

}
