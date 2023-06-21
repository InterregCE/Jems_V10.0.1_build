package io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration

import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportPartnerCheckPlugin
import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins
import io.cloudflight.jems.server.call.service.preSubmissionCheckSettingsUpdated
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

@Service
class UpdatePreSubmissionCheckSettings(
    private val persistence: CallPersistence,
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val auditPublisher: ApplicationEventPublisher
    ) : UpdatePreSubmissionCheckSettingsInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UpdatePreSubmissionCheckSettingsException::class)
    override fun update(callId: Long, newPluginsConfig: PreSubmissionPlugins): CallDetail {
        val call = persistence.getCallById(callId)
        val oldPluginsConfig = call.toPluginModel()
        val changes = newPluginsConfig.getDiff(oldPluginsConfig)

        return if (isSelectedPluginKeyValid(call.is2StepCall(), newPluginsConfig) && changes.isNotEmpty())
            persistence.updateProjectCallPreSubmissionCheckPlugin(callId, newPluginsConfig)
                .also { auditPublisher.publishEvent(preSubmissionCheckSettingsUpdated(this, changes, it)) }
        else
            call
    }

    private fun isSelectedPluginKeyValid(hasTwoSteps: Boolean, pluginKeys: PreSubmissionPlugins): Boolean {
        val validStep1Check = !hasTwoSteps || exists(pluginKeys.firstStepPluginKey, PreConditionCheckPlugin::class)
        val validStep2Check = exists(pluginKeys.pluginKey, PreConditionCheckPlugin::class)
        val validReportPartnerCheck = exists(pluginKeys.reportPartnerCheckPluginKey, ReportPartnerCheckPlugin::class)

        return validStep1Check && validStep2Check && validReportPartnerCheck
    }

    private fun <T : JemsPlugin> exists(pluginKey: String?, type: KClass<T>) =
        jemsPluginRegistry.get(type, pluginKey).getKey().isNotEmpty()

    private fun CallDetail.toPluginModel() = PreSubmissionPlugins(
        pluginKey = preSubmissionCheckPluginKey ?: "",
        firstStepPluginKey = firstStepPreSubmissionCheckPluginKey ?: "",
        reportPartnerCheckPluginKey = reportPartnerCheckPluginKey ?: "",
        reportProjectCheckPluginKey = reportProjectCheckPluginKey ?: "",
        controlReportPartnerCheckPluginKey = controlReportPartnerCheckPluginKey ?: "",
        controlReportSamplingCheckPluginKey = controlReportSamplingCheckPluginKey?: ""
    )
}
