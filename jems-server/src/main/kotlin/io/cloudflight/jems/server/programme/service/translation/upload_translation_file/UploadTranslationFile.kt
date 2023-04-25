package io.cloudflight.jems.server.programme.service.translation.upload_translation_file

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.programmeTranslationFileUploaded
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import org.apache.commons.io.IOUtils
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream
import java.io.InputStream

@Service
class UploadTranslationFile(
    private val translationFilePersistence: TranslationFilePersistence,
    private val messageSource: ReloadableResourceBundleMessageSource,
    private val eventPublisher: ApplicationEventPublisher,
    private val appProperties: AppProperties
) : UploadTranslationFileInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(UploadTranslationFileFailed::class)
    override fun upload(
        fileType: TranslationFileType, language: SystemLanguage, inputStream: InputStream, size: Long
    ): TranslationFileMetaData {

        if (translationFilePersistence.exists(fileType, language))
            translationFilePersistence.archiveTranslationFile(fileType, language)

        val byteArray = IOUtils.toByteArray(inputStream)

        return translationFilePersistence.save(fileType, language, ByteArrayInputStream(byteArray), size).also {
            eventPublisher.publishEvent(programmeTranslationFileUploaded(this, fileType.getFileNameFor(language)))
            copyTranslationFiles(appProperties.translationsFolder, byteArray, fileType.getFileNameFor(language))
            //refreshes translation files without restarting the system
            messageSource.clearCache()
        }
    }

}
