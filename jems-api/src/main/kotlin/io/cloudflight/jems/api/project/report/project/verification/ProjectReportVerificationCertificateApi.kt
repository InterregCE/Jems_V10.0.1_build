package io.cloudflight.jems.api.project.report.project.verification

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Report Verification Certificate")
interface ProjectReportVerificationCertificateApi {

    companion object {
        const val ENDPOINT_API_PROJECT_REPORT_VERIFICATION_CERTIFICATE = "$ENDPOINT_API_PROJECT_REPORT_VERIFICATION/certificate/"
    }

    @ApiOperation("List certificates for Project Report Verification")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_VERIFICATION_CERTIFICATE)
    fun list(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        pageable: Pageable,
    ): Page<JemsFileDTO>

    @ApiOperation("Update description of certificate for Project Report Verification")
    @PutMapping(
        "$ENDPOINT_API_PROJECT_REPORT_VERIFICATION_CERTIFICATE/byFileId/{fileId}/description",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateDescription(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long,
        @RequestBody(required = false) description: String?,
    )

    @ApiOperation("Download certificate from Project Report Verification")
    @GetMapping(
        "$ENDPOINT_API_PROJECT_REPORT_VERIFICATION_CERTIFICATE/byFileId/{fileId}/",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun download(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long,
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Generate certificate for Project Report Verification")
    @GetMapping("$ENDPOINT_API_PROJECT_REPORT_VERIFICATION_CERTIFICATE/certificate")
    fun generate(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestParam(required = true) pluginKey: String
    )

}
