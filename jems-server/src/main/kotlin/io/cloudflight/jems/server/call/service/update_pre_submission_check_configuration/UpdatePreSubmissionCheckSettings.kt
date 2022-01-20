package io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration

import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
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
    override fun update(callId: Long, pluginKey: String?) =
        ifSelectedPluginKeyIsValid(pluginKey).run {
            persistence.updateProjectCallPreSubmissionCheckPlugin(callId, pluginKey)
        }

    private fun ifSelectedPluginKeyIsValid(pluginKey: String?) =
        jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKey)
}
