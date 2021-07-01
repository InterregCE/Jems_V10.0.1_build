package io.cloudflight.jems.server.programme.service.translation.upload_translation_file

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import java.io.InputStream

interface UploadTranslationFileInteractor {

    fun upload(fileType: TranslationFileType, language: SystemLanguage, inputStream: InputStream, size: Long): TranslationFileMetaData

}
