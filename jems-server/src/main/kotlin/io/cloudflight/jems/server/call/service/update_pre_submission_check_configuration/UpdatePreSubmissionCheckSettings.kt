package io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration

import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePreSubmissionCheckSettings(
    private val persistence: CallPersistence,
    private val jemsPluginRegistry: JemsPluginRegistry
) : UpdatePreSubmissionCheckSettingsInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UpdatePreSubmissionCheckSettingsException::class)
    override fun update(callId: Long, pluginKeys: PreSubmissionPlugins): CallDetail {
        val call = persistence.getCallById(callId)

        return if (isSelectedPluginKeyValid(call.is2StepCall(), pluginKeys))
            persistence.updateProjectCallPreSubmissionCheckPlugin(callId, pluginKeys)
        else
            call
    }

    private fun isSelectedPluginKeyValid(hasTwoSteps: Boolean, pluginKeys: PreSubmissionPlugins): Boolean {
        if (hasTwoSteps) {
            return jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKeys.pluginKey).getKey().isNotEmpty()
                && jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKeys.firstStepPluginKey).getKey().isNotEmpty()
        }
        return jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKeys.pluginKey).getKey().isNotEmpty()
    }

}
