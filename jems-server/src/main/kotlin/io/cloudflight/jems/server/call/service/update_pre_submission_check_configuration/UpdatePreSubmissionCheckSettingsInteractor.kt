package io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration

import io.cloudflight.jems.server.call.service.model.CallDetail


interface UpdatePreSubmissionCheckSettingsInteractor {
    fun update(callId: Long, pluginKey: String?): CallDetail
}
