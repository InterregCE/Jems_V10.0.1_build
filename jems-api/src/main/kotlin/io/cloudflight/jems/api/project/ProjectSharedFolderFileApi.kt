package io.cloudflight.jems.api.project

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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Api("Project Shared Folder File")
interface ProjectSharedFolderFileApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_SHARED_FOLDER = "/api/project/{projectId}/sharedFolder"
    }

    @ApiOperation("List Shared Folder files")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @PostMapping("$ENDPOINT_API_PROJECT_SHARED_FOLDER/list")
    fun listSharedFolderFiles(
        @PathVariable projectId: Long,
        pageable: Pageable,
    ): Page<ProjectReportFileDTO>

    @ApiOperation("Upload file to Shared Folder")
    @PostMapping("$ENDPOINT_API_PROJECT_SHARED_FOLDER/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFileToSharedFolder(
        @PathVariable projectId: Long,
        @RequestPart("file") file: MultipartFile,
    ): ProjectReportFileMetadataDTO

    @ApiOperation("Download file from Shared Folder")
    @GetMapping("$ENDPOINT_API_PROJECT_SHARED_FOLDER/byFileId/{fileId}/download", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadSharedFolderFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Set description for Shared Folder File")
    @PutMapping("$ENDPOINT_API_PROJECT_SHARED_FOLDER/byFileId/{fileId}/description", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setDescriptionToSharedFolderFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long,
        @RequestBody(required = false) description: String?
    )

    @ApiOperation("Delete file from Shared Folder")
    @DeleteMapping("$ENDPOINT_API_PROJECT_SHARED_FOLDER/byFileId/{fileId}/delete")
    fun deleteSharedFolderFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long,
    )
}
