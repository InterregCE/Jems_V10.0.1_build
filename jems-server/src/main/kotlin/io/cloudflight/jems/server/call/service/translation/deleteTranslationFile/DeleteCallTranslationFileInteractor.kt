package io.cloudflight.jems.server.call.service.translation.deleteTranslationFile

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

interface DeleteCallTranslationFileInteractor {

    fun delete(callId: Long, language: SystemLanguage)

}
