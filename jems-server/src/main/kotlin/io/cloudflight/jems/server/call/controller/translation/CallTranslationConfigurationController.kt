package io.cloudflight.jems.server.call.controller.translation

import io.cloudflight.jems.api.call.CallTranslationConfigurationApi
import io.cloudflight.jems.api.call.dto.translation.CallTranslationFileDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.call.service.translation.deleteTranslationFile.DeleteCallTranslationFileInteractor
import io.cloudflight.jems.server.call.service.translation.downloadTranslationFile.DownloadCallTranslationFileInteractor
import io.cloudflight.jems.server.call.service.translation.getTranslation.GetTranslationInteractor
import io.cloudflight.jems.server.call.service.translation.uploadTranslationFile.UploadCallTranslationFileInteractor
import io.cloudflight.jems.server.common.toResponseFile
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class CallTranslationConfigurationController(
    private val getTranslation: GetTranslationInteractor,
    private val downloadTranslationFile: DownloadCallTranslationFileInteractor,
    private val uploadTranslationFile: UploadCallTranslationFileInteractor,
    private val deleteTranslationFile: DeleteCallTranslationFileInteractor,
) : CallTranslationConfigurationApi {

    override fun getTranslation(callId: Long): List<CallTranslationFileDTO> =
        getTranslation.get(callId).toDto()

    override fun downloadTranslationFile(callId: Long, fileId: Long): ResponseEntity<ByteArrayResource> =
        downloadTranslationFile.download(callId, fileId = fileId).toResponseFile()

    override fun uploadTranslationFile(
        callId: Long,
        language: SystemLanguage,
        file: MultipartFile,
    ): CallTranslationFileDTO =
        uploadTranslationFile.upload(callId = callId, language, file.toProjectFile()).toDto()

    override fun deleteTranslationFile(callId: Long, language: SystemLanguage) =
        deleteTranslationFile.delete(callId = callId, language)

}
