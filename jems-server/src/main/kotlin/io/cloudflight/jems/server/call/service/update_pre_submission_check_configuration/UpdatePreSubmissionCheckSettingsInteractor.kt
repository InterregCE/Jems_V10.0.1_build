package io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration

import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins


interface UpdatePreSubmissionCheckSettingsInteractor {
    fun update(callId: Long, pluginKeys: PreSubmissionPlugins): CallDetail
}
