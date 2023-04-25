package io.cloudflight.jems.server.call.service.translation.uploadTranslationFile

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.call.service.model.translation.CallTranslationFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadCallTranslationFileInteractor {

    fun upload(callId: Long, language: SystemLanguage, file: ProjectFile): CallTranslationFile

}
