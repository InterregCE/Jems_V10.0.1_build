package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.ProjectReportResultPrincipleDTO
import io.cloudflight.jems.api.project.dto.report.project.projectResults.UpdateProjectReportResultPrincipleDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
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

@Api("Project Report Result Principle")
interface ProjectReportResultPrincipleApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_PROJECT_RESULTS =
            "${ProjectReportApi.ENDPOINT_API_PROJECT_REPORT}/resultPrinciple"
    }

    @ApiOperation("Returns project report project results and horizontal principles")
    @GetMapping(ENDPOINT_API_PROJECT_REPORT_PROJECT_RESULTS)
    fun getResultAndPrinciple(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long
    ): ProjectReportResultPrincipleDTO

    @ApiOperation("Updates project report project results and horizontal principles")
    @PutMapping(ENDPOINT_API_PROJECT_REPORT_PROJECT_RESULTS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateResultAndPrinciple(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody resultPrinciple: UpdateProjectReportResultPrincipleDTO
    ): ProjectReportResultPrincipleDTO

    @ApiOperation("Upload attachment to project report project result")
    @PostMapping(
        "$ENDPOINT_API_PROJECT_REPORT_PROJECT_RESULTS/byProjectResult/{resultNumber}/attachment",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadAttachmentToResult(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable resultNumber: Int,
        @RequestPart("file") file: MultipartFile
    ): ProjectReportFileMetadataDTO

    @ApiOperation("Download attachment from project report project result")
    @GetMapping(
        "$ENDPOINT_API_PROJECT_REPORT_PROJECT_RESULTS/byProjectResult/{resultNumber}/attachment",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun downloadAttachmentFromResult(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable resultNumber: Int
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Delete attachment from project report project result")
    @DeleteMapping("$ENDPOINT_API_PROJECT_REPORT_PROJECT_RESULTS/byProjectResult/{resultNumber}/attachment")
    fun deleteAttachmentFromResult(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable resultNumber: Int
    )
}
