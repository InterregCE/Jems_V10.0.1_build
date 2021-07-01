package io.cloudflight.jems.server.programme.service.translation.download_translation_file

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadTranslationFile(
    private val translationFilePersistence: TranslationFilePersistence,
) : DownloadTranslationFileInteractor {


    @CanRetrieveProgrammeSetup
    @ExceptionWrapper(DownloadTranslationFileFailed::class)
    override fun download(fileType: TranslationFileType, language: SystemLanguage) =
        translationFilePersistence.getTranslationFile(fileType, language)

}
