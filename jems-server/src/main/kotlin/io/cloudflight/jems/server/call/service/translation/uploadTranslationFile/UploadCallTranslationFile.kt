package io.cloudflight.jems.server.call.service.translation.uploadTranslationFile

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.model.translation.CallTranslationFile
import io.cloudflight.jems.server.call.service.translation.LINE_ENDING
import io.cloudflight.jems.server.call.service.translation.PROPERTIES_CHARSET
import io.cloudflight.jems.server.call.service.translation.addCallIdPrefixes
import io.cloudflight.jems.server.call.service.translation.ifContentOrNull
import io.cloudflight.jems.server.call.service.translation.updateCallTranslations
import io.cloudflight.jems.server.call.service.translation.withoutEmptyLines
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.CallTranslation
import io.cloudflight.jems.server.config.AppProperties
import io.cloudflight.jems.server.programme.service.translation.upload_translation_file.copyTranslationFiles
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class UploadCallTranslationFile(
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsSystemFileService,
    private val securityService: SecurityService,
    private val appProperties: AppProperties,
    private val messageSource: ReloadableResourceBundleMessageSource,
) : UploadCallTranslationFileInteractor {

    companion object {

        fun storageFileNameFor(callId: Long, language: SystemLanguage) =
            "call-id-$callId-Application_${language.name.lowercase()}.properties"

        fun archivedFileNameFor(originalFileName: String) =
            "archived-${ZonedDateTime.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss"))}-$originalFileName"

        fun classpathFileNameFor(language: SystemLanguage) =
            "Application-CallSpecific_${language.name.lowercase()}.properties"
    }

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UploadCallTranslationFileException::class)
    override fun upload(callId: Long, language: SystemLanguage, file: ProjectFile): CallTranslationFile {
        val processedLines = file.stream.addCallIdPrefixes(callId) // file as is, just prefixed with callId
        val updatedLinesOnlyNotEmpty = processedLines.withoutEmptyLines()
        // content, which is used for auto-refresh
        val content = updatedLinesOnlyNotEmpty.joinToString(LINE_ENDING).toByteArray(PROPERTIES_CHARSET)

        if (updatedLinesOnlyNotEmpty.size > 5000)
            throw TooManyCallSpecificTranslations()

        val fileName = storageFileNameFor(callId, language)
        archiveFileIfThereIsOldOne(callId, fileName)

        val storedFile = processedLines.ifContentOrNull()?.toModel(callId, language, fileName)
            ?.let { fileToStore -> fileService.persistFile(fileToStore) }

        return CallTranslationFile(language, storedFile?.toSimple(), null).also {
            copyFileAndAutoRefresh(callId, language, content)
        }
    }


    private fun copyFileAndAutoRefresh(callId: Long, language: SystemLanguage, content: ByteArray) {
        val callSpecificFile = classpathFileNameFor(language)
        val callSpecificFilePath = Path.of(appProperties.translationsFolder, callSpecificFile)

        if (!Files.exists(callSpecificFilePath))
            copyTranslationFiles(appProperties.translationsFolder, content, callSpecificFile)
        else
            updateCallTranslations(callId, callSpecificFilePath, content)

        messageSource.clearCache()
    }

    private fun archiveFileIfThereIsOldOne(callId: Long, fileName: String) {
        filePersistence.fileIdIfExists(exactPath = CallTranslation.generatePath(callId), fileName = fileName)?.let { previousFileId ->
            fileService.archiveCallTranslation(
                previousFileId,
                newName = archivedFileNameFor(fileName),
                newLocation = JemsFileType.CallTranslationArchive.generatePath(callId),
            )
        }
    }

    private fun ByteArray.toModel(callId: Long, language: SystemLanguage, fileName: String) = JemsFileCreate(
        projectId = null,
        partnerId = null,
        name = fileName,
        path = CallTranslation.generatePath(callId),
        type = CallTranslation,
        size = this.size.toLong(),
        content = ByteArrayInputStream(this),
        userId = securityService.getUserIdOrThrow(),
        defaultDescription = language.name,
    )

}
