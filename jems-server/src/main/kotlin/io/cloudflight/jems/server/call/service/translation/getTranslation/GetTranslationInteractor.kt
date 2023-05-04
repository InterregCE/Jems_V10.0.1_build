package io.cloudflight.jems.server.call.service.translation.getTranslation

import io.cloudflight.jems.server.call.service.model.translation.CallTranslationFile

interface GetTranslationInteractor {

    fun get(callId: Long): List<CallTranslationFile>

}
