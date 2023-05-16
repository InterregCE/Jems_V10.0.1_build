package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.file.ProjectFileCategoryDTO
import io.cloudflight.jems.api.project.dto.file.ProjectFileMetadataDTO
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

@Api("Project File")
interface ProjectFileApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_FILE = "/api/project/{projectId}/file"
    }

    @ApiOperation("Upload file to project")
    @PostMapping("$ENDPOINT_API_PROJECT_FILE/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "type", dataType = "string", required = true),
        ApiImplicitParam(paramType = "query", name = "id", dataType = "number"),
    )
    fun uploadFile(
        @PathVariable projectId: Long,
        fileCategory: ProjectFileCategoryDTO,
        @RequestPart("file") file: MultipartFile
    ): ProjectFileMetadataDTO

    @ApiOperation("Download file from project")
    @GetMapping("$ENDPOINT_API_PROJECT_FILE/download/{fileId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("list project files")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "type", dataType = "string", required = true),
        ApiImplicitParam(paramType = "query", name = "id", dataType = "number"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer")
    )
    @GetMapping("$ENDPOINT_API_PROJECT_FILE/list")
    fun listProjectFiles(
        @PathVariable projectId: Long,
        fileCategory: ProjectFileCategoryDTO,
        pageable: Pageable
    ): Page<ProjectFileMetadataDTO>

    @ApiOperation("Specify description for project file")
    @PutMapping("$ENDPOINT_API_PROJECT_FILE/{fileId}/description", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun setProjectFileDescription(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long,
        @RequestBody description: String?
    ): ProjectFileMetadataDTO

    @ApiOperation("Delete existing file")
    @DeleteMapping("$ENDPOINT_API_PROJECT_FILE/{fileId}")
    fun deleteProjectFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long
    )

}
