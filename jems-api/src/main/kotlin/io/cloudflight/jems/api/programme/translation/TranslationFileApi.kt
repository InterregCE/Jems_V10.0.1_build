package io.cloudflight.jems.api.programme.translation

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.translation.TranslationFileMetaDataDTO
import io.cloudflight.jems.api.programme.dto.translation.TranslationFileTypeDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Api("Translation File")
interface TranslationFileApi {

    companion object {
        private const val ENDPOINT_API_TRANSLATION_FILE = "/api/translationFile"
    }

    @ApiOperation("Retrieve list of translation files")
    @GetMapping(ENDPOINT_API_TRANSLATION_FILE)
    fun get(): List<TranslationFileMetaDataDTO>

    @ApiOperation("Upload translation file")
    @PostMapping("$ENDPOINT_API_TRANSLATION_FILE/{fileType}/{language}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @PathVariable fileType: TranslationFileTypeDTO,
        @PathVariable language: SystemLanguage,
        @RequestPart("file") file: MultipartFile
    ): TranslationFileMetaDataDTO

    @ApiOperation("Download translation file")
    @GetMapping("$ENDPOINT_API_TRANSLATION_FILE/{fileType}/{language}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun download(
        @PathVariable fileType: TranslationFileTypeDTO,
        @PathVariable language: SystemLanguage,
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Download default en translation file")
    @GetMapping("$ENDPOINT_API_TRANSLATION_FILE/{fileType}", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadDefaultEnTranslationFile(
        @PathVariable fileType: TranslationFileTypeDTO
    ): ResponseEntity<ByteArrayResource>
}
