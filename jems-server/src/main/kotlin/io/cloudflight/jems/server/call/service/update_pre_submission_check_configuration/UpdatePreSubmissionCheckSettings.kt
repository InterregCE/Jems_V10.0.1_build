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
    override fun update(callId: Long, pluginKeys: PreSubmissionPlugins): CallDetail {
        val call = persistence.getCallById(callId)

        return if (isSelectedPluginKeyValid(call.is2StepCall(), pluginKeys))
            persistence.updateProjectCallPreSubmissionCheckPlugin(callId, pluginKeys)
                .also { auditPublisher.publishEvent(preSubmissionCheckSettingsUpdated(this, pluginKeys, call.toPluginModel(), it)) }
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
        firstStepPluginKey = firstStepPreSubmissionCheckPluginKey,
        pluginKey = preSubmissionCheckPluginKey ?: "",
        reportPartnerCheckPluginKey = reportPartnerCheckPluginKey ?: ""
    )
}
