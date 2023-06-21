package io.cloudflight.jems.api.project.report.project

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileSearchRequestDTO
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

@Api("Project Report Annexes")
interface ProjectReportAnnexesApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_REPORT_ANNEXES =
            "${ProjectReportApi.ENDPOINT_API_PROJECT_REPORT_PREFIX}/byReportId/{reportId}"
    }

    @ApiOperation("List project report annexes")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @PostMapping(
        "${ENDPOINT_API_PROJECT_REPORT_ANNEXES}/list",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getProjectReportAnnexes(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        pageable: Pageable,
        @RequestBody searchRequest: ProjectReportFileSearchRequestDTO,
    ): Page<JemsFileDTO>

    @ApiOperation("Upload file to project report annexes")
    @PostMapping(
        "${ENDPOINT_API_PROJECT_REPORT_ANNEXES}/upload",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadProjectReportAnnexesFile(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestPart("file") file: MultipartFile,
    ): JemsFileMetadataDTO

    @ApiOperation("Update description of an existing file in project report annexes")
    @PutMapping(
        "${ENDPOINT_API_PROJECT_REPORT_ANNEXES}/byFileId/{fileId}/description",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateProjectReportAnnexesFileDescription(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long,
        @RequestBody(required = false) description: String?
    )

    @ApiOperation("Delete an existing file in project report annexes")
    @DeleteMapping("${ENDPOINT_API_PROJECT_REPORT_ANNEXES}/byFileId/{fileId}/delete")
    fun deleteProjectReportAnnexesFile(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long
    )

    @ApiOperation("Download an existing file in project report file annexes")
    @GetMapping(
        "${ENDPOINT_API_PROJECT_REPORT_ANNEXES}/byFileId/{fileId}/download",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun downloadProjectReportAnnexesFile(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable fileId: Long
    ): ResponseEntity<ByteArrayResource>
}
