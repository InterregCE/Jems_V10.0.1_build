package io.cloudflight.jems.api.project.contracting

import io.cloudflight.jems.api.project.dto.contracting.file.ProjectContractingFileSearchRequestDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.multipart.MultipartFile

@Api("Project Contracting File Management")
interface ContractingFileApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_FILE = "/api/project/{projectId}/contracting/file"
    }

    @ApiOperation("Upload contract to contracting")
    @PostMapping("$ENDPOINT_API_CONTRACTING_FILE/contract", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadContractFile(
        @PathVariable projectId: Long,
        @RequestPart("file") file: MultipartFile,
    ): ProjectReportFileMetadataDTO

    @ApiOperation("Upload other contract document to contracting")
    @PostMapping("$ENDPOINT_API_CONTRACTING_FILE/contractDocument", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadContractDocumentFile(
        @PathVariable projectId: Long,
        @RequestPart("file") file: MultipartFile,
    ): ProjectReportFileMetadataDTO

    @ApiOperation("Upload contract document to partner in contracting section")
    @PostMapping("$ENDPOINT_API_CONTRACTING_FILE/partnerDocument/{partnerId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadContractFileForPartner(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @RequestPart("file") file: MultipartFile,
    ): ProjectReportFileMetadataDTO

    @ApiOperation("Upload contract document to partner in contracting section")
    @PostMapping("$ENDPOINT_API_CONTRACTING_FILE/internal", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadContractInternalFile(
        @PathVariable projectId: Long,
        @RequestPart("file") file: MultipartFile,
    ): ProjectReportFileMetadataDTO

    @ApiOperation("List contracting files")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @PostMapping(
        value = [
            "${ENDPOINT_API_CONTRACTING_FILE}/list/byPartnerId/{partnerId}",
            "${ENDPOINT_API_CONTRACTING_FILE}/list",
        ],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun listFiles(
        @PathVariable projectId: Long,
        @PathVariable(required = false) partnerId: Long? = null,
        pageable: Pageable,
        @RequestBody searchRequest: ProjectContractingFileSearchRequestDTO,
    ): Page<ProjectReportFileDTO>

    @ApiOperation("Update description of already uploaded file")
    @PutMapping(
        "${ENDPOINT_API_CONTRACTING_FILE}/contract/{fileId}/description",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun updateContractFileDescription(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long,
        @RequestBody(required = false) description: String?,
    )


    @ApiOperation("Update description of already uploaded file")
    @PutMapping(
        "${ENDPOINT_API_CONTRACTING_FILE}/monitoring/{fileId}/description",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun updateInternalFileDescription(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long,
        @RequestBody(required = false) description: String?,
    )

    @ApiOperation("Download contract file")
    @GetMapping(
        "${ENDPOINT_API_CONTRACTING_FILE}/contract/download/{fileId}",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun downloadContractFile(@PathVariable projectId: Long, @PathVariable fileId: Long): ResponseEntity<ByteArrayResource>

    @ApiOperation("Download internal file")
    @GetMapping(
        "${ENDPOINT_API_CONTRACTING_FILE}/internal/download/{fileId}",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun downloadInternalFile(@PathVariable projectId: Long, @PathVariable fileId: Long): ResponseEntity<ByteArrayResource>

    @ApiOperation("Delete contract file")
    @DeleteMapping("${ENDPOINT_API_CONTRACTING_FILE}/contract/delete/{fileId}")
    fun deleteContractFile(@PathVariable projectId: Long, @PathVariable fileId: Long)

    @ApiOperation("Delete internal file")
    @DeleteMapping("${ENDPOINT_API_CONTRACTING_FILE}/monitoring/delete/{fileId}")
    fun deleteInternalFile(@PathVariable projectId: Long, @PathVariable fileId: Long)
}
