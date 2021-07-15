package io.cloudflight.jems.server.programme.controller.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.translation.TranslationFileMetaDataDTO
import io.cloudflight.jems.api.programme.dto.translation.TranslationFileTypeDTO
import io.cloudflight.jems.api.programme.translation.TranslationFileApi
import io.cloudflight.jems.server.programme.service.translation.download_translation_file.DownloadTranslationFileInteractor
import io.cloudflight.jems.server.programme.service.translation.list_translation_files.ListTranslationFilesInteractor
import io.cloudflight.jems.server.programme.service.translation.upload_translation_file.UploadTranslationFileInteractor
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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

    override fun download(
        fileType: TranslationFileTypeDTO, language: SystemLanguage
    ): ResponseEntity<ByteArrayResource> =
        with(fileType.toModel()) {
            getFileResponse(
                downloadTranslationFile.download(this, language),
                this.getFileNameFor(language)
            )
        }

    override fun downloadDefaultEnTranslationFile(fileType: TranslationFileTypeDTO): ResponseEntity<ByteArrayResource> =
        with(fileType.toModel()) {
            getFileResponse(
                downloadTranslationFile.downloadDefaultEnTranslationFile(this),
                this.getFileNameFor(SystemLanguage.EN)
            )
        }


    private fun getFileResponse(file: ByteArray, fileName: String): ResponseEntity<ByteArrayResource> =
        ResponseEntity.ok()
            .contentLength(file.size.toLong())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"$fileName\""
            ).body(ByteArrayResource(file))
}
