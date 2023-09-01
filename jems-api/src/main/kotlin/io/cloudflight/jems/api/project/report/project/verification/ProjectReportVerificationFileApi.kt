package io.cloudflight.jems.api.project.report.project.verification

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.report.project.verification.ProjectReportVerificationApi.Companion.ENDPOINT_API_PROJECT_REPORT_VERIFICATION
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Api("Project Report Verification File")
interface ProjectReportVerificationFileApi {

    companion object {
        const val ENDPOINT_API_PROJECT_REPORT_VERIFICATION_FILE = "$ENDPOINT_API_PROJECT_REPORT_VERIFICATION/file/"
    }

    @ApiOperation("List files for Project Report Verification")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_VERIFICATION_FILE)
    fun list(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        pageable: Pageable,
    ): Page<JemsFileDTO>

    @ApiOperation("Upload file to Project Report Verification")
    @PostMapping(
        ENDPOINT_API_PROJECT_REPORT_VERIFICATION_FILE,
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun upload(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestPart("file") file: MultipartFile,
    ): JemsFileMetadataDTO

    @ApiOperation("Update description for Project Report Verification")
    @PutMapping(
        "$ENDPOINT_API_PROJECT_REPORT_VERIFICATION_FILE/byFileId/{fileId}/description",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateDescription(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long,
        @RequestBody(required = false) description: String?,
    )

    @ApiOperation("Download file from Project Report Verification")
    @GetMapping(
        "$ENDPOINT_API_PROJECT_REPORT_VERIFICATION_FILE/byFileId/{fileId}/",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun download(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long,
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Delete file from Project Report Verification")
    @DeleteMapping(
        "$ENDPOINT_API_PROJECT_REPORT_VERIFICATION_FILE/byFileId/{fileId}/",
    )
    fun delete(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long,
    )
}
