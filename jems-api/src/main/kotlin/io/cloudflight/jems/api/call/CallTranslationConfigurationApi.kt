package io.cloudflight.jems.api.call

import io.cloudflight.jems.api.call.CallApi.Companion.ENDPOINT_API_CALL
import io.cloudflight.jems.api.call.dto.translation.CallTranslationFileDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Api("CallTranslationConfiguration")
interface CallTranslationConfigurationApi {

    companion object {
        private const val ENDPOINT_API_CALL_TRANSLATION_CONFIG = "$ENDPOINT_API_CALL/translation/{callId}"
    }

    @ApiOperation("Returns list of all translation files")
    @GetMapping(ENDPOINT_API_CALL_TRANSLATION_CONFIG)
    fun getTranslation(@PathVariable callId: Long): List<CallTranslationFileDTO>

    @ApiOperation("Download translation file")
    @GetMapping("$ENDPOINT_API_CALL_TRANSLATION_CONFIG/download/{fileId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadTranslationFile(@PathVariable callId: Long, @PathVariable fileId: Long): ResponseEntity<ByteArrayResource>

    @ApiOperation("Upload translation file")
    @PostMapping("$ENDPOINT_API_CALL_TRANSLATION_CONFIG/upload/{language}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadTranslationFile(
        @PathVariable callId: Long,
        @PathVariable language: SystemLanguage,
        @RequestPart("file") file: MultipartFile,
    ): CallTranslationFileDTO

    @ApiOperation("Delete translation file")
    @DeleteMapping("$ENDPOINT_API_CALL_TRANSLATION_CONFIG/delete/{language}")
    fun deleteTranslationFile(@PathVariable callId: Long, @PathVariable language: SystemLanguage)

}
