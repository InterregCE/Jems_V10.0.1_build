package io.cloudflight.jems.server.call.service.translation.deleteTranslationFile

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.translation.updateCallTranslations
import io.cloudflight.jems.server.call.service.translation.uploadTranslationFile.UploadCallTranslationFile
import io.cloudflight.jems.server.call.service.translation.uploadTranslationFile.UploadCallTranslationFile.Companion.archivedFileNameFor
import io.cloudflight.jems.server.call.service.translation.uploadTranslationFile.UploadCallTranslationFile.Companion.storageFileNameFor
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.CallTranslation
import io.cloudflight.jems.server.config.AppProperties
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.nio.file.Files
import java.nio.file.Path

@Service
class DeleteCallTranslationFile(
    private val filePersistence: JemsFilePersistence,
    private val fileService: JemsSystemFileService,
    private val appProperties: AppProperties,
    private val messageSource: ReloadableResourceBundleMessageSource,
) : DeleteCallTranslationFileInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(DeleteCallTranslationFileException::class)
    override fun delete(callId: Long, language: SystemLanguage) {
        val fileName = storageFileNameFor(callId, language)
        val fileId = filePersistence.fileIdIfExists(exactPath = CallTranslation.generatePath(callId), fileName = fileName)
            ?: throw FileNotFound()

        fileService.moveFile(
            fileId,
            newName = archivedFileNameFor(fileName),
            newLocation = JemsFileType.CallTranslationArchive.generatePath(callId),
        )

        deleteCallTranslationsFromFileAndAutoRefresh(callId, language)
    }

    private fun deleteCallTranslationsFromFileAndAutoRefresh(callId: Long, language: SystemLanguage) {
        val callSpecificFile = UploadCallTranslationFile.classpathFileNameFor(language)
        val callSpecificFilePath = Path.of(appProperties.translationsFolder, callSpecificFile)

        if (Files.exists(callSpecificFilePath)) {
            updateCallTranslations(callId, callSpecificFilePath, ByteArray(0))
            messageSource.clearCache()
        }
    }

}
