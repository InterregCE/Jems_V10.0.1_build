package io.cloudflight.jems.server.programme.controller.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.translation.TranslationFileMetaDataDTO
import io.cloudflight.jems.api.programme.dto.translation.TranslationFileTypeDTO
import io.cloudflight.jems.api.programme.translation.TranslationFileApi
import io.cloudflight.jems.server.common.toResponseFile
import io.cloudflight.jems.server.programme.service.translation.download_translation_file.DownloadTranslationFileInteractor
import io.cloudflight.jems.server.programme.service.translation.list_translation_files.ListTranslationFilesInteractor
import io.cloudflight.jems.server.programme.service.translation.upload_translation_file.UploadTranslationFileInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class TranslationFileController(
    private val downloadTranslationFile: DownloadTranslationFileInteractor,
    private val uploadTranslationFile: UploadTranslationFileInteractor,
    private val listTranslationFiles: ListTranslationFilesInteractor
) : TranslationFileApi {


    override fun get(): List<TranslationFileMetaDataDTO> =
        listTranslationFiles.list().toDTO()

    override fun upload(
        fileType: TranslationFileTypeDTO, language: SystemLanguage, file: MultipartFile
    ): TranslationFileMetaDataDTO =
        uploadTranslationFile.upload(fileType.toModel(), language, file.inputStream, file.size).toDTO()

    override fun download(fileType: TranslationFileTypeDTO, language: SystemLanguage): ResponseEntity<ByteArrayResource> =
        with(fileType.toModel()) {
            val fileName = this.getFileNameFor(language)
            val file = downloadTranslationFile.download(this, language)
            return@with fileName to file
        }.toResponseFile()

    override fun downloadDefaultEnTranslationFile(fileType: TranslationFileTypeDTO): ResponseEntity<ByteArrayResource> =
        with(fileType.toModel()) {
            val fileName = this.getFileNameFor(SystemLanguage.EN)
            val file = downloadTranslationFile.downloadDefaultEnTranslationFile(this)
            return@with fileName to file
        }.toResponseFile()
}

