package io.cloudflight.jems.server.programme.service.translation.upload_translation_file

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.config.AppResourcesProperties
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.programmeTranslationFileUploaded
import io.cloudflight.jems.server.programme.service.translation.TranslationFilePersistence
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileMetaData
import io.cloudflight.jems.server.programme.service.translation.model.TranslationFileType
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import org.apache.commons.io.IOUtils
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadTranslationFile(
    private val translationFilePersistence: TranslationFilePersistence,
    private val messageSource: ReloadableResourceBundleMessageSource,
    private val eventPublisher: ApplicationEventPublisher,
    private val appResourcesProperties: AppResourcesProperties
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

        return translationFilePersistence.save(
            fileType,
            language,
            ByteArrayInputStream(byteArray),
            size
        ).also {
            eventPublisher.publishEvent(programmeTranslationFileUploaded(this, fileType.getFileNameFor(language)))
            copyTranslationFiles(byteArray, fileType, language)
            //refreshes translation files without restarting the system
            messageSource.clearCache()
        }
    }

    private fun copyTranslationFiles(
        byteArray: ByteArray,
        fileType: TranslationFileType,
        language: SystemLanguage,
    ) {
        val translationsFolderPath = getTranslationsFolder()
        val translationsFolder = File(translationsFolderPath)

        val translationFilePath = translationsFolderPath.plus(fileType.getFileNameFor(language))

        if (!translationsFolder.exists())
            translationsFolder.mkdirs()
        val translationFile = File(translationFilePath)
        val outputStream: FileOutputStream = if (translationFile.exists())
            FileOutputStream(translationFile)
        else {
            translationFile.createNewFile()
            FileOutputStream(translationFilePath)
        }
        outputStream.write(byteArray)
        outputStream.close()
    }

    private fun getTranslationsFolder(): String =
        with(appResourcesProperties.translationsFolder) {
            var result = this
            if (!result.endsWith("/"))
                result = result.plus("/")

            if (!result.startsWith("./"))
                result = "./".plus(result)

            result
        }

}