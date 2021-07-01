package io.cloudflight.jems.server.programme.service.translation.upload_translation_file

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream

@Service
class UploadTranslationFile(
    private val translationFilePersistence: TranslationFilePersistence,
    private val eventPublisher: ApplicationEventPublisher
) : UploadTranslationFileInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(UploadTranslationFileFailed::class)
    override fun upload(
        fileType: TranslationFileType, language: SystemLanguage, inputStream: InputStream, size: Long
    ): TranslationFileMetaData {

        if (translationFilePersistence.exists(fileType, language))
            translationFilePersistence.archiveTranslationFile(fileType, language)

        return translationFilePersistence.save(fileType, language, inputStream, size).also {
            eventPublisher.publishEvent(UploadTranslationFileEvent(this, it, inputStream))
        }
    }

}
