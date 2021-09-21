package io.cloudflight.jems.server.programme.service.translation.download_translation_file

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType

interface DownloadTranslationFileInteractor {

    fun download(fileType: TranslationFileType, language: SystemLanguage): ByteArray

    fun downloadDefaultEnTranslationFile(fileType: TranslationFileType): ByteArray

}
