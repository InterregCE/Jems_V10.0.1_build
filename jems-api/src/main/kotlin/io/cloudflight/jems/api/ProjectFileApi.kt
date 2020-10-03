package io.cloudflight.jems.api

import io.cloudflight.jems.api.dto.InputProjectFileDescription
import io.cloudflight.jems.api.dto.OutputProjectFile
import io.cloudflight.jems.api.dto.ProjectFileType
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid

@Api("Project File Storage")
@RequestMapping("/api/project/{projectId}/file")
interface ProjectFileApi {

    @ApiOperation("Upload application file to project")
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProjectFile(
        @PathVariable projectId: Long,
        @RequestParam("fileType") fileType: ProjectFileType,
        @RequestPart("file") file: MultipartFile
    )

    @ApiOperation("Download file")
    @GetMapping("/{fileId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Get list of application files")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer")
    )
    @GetMapping()
    fun getFilesForProject(
        @PathVariable projectId: Long,
        @RequestParam("fileType") fileType: ProjectFileType,
        pageable: Pageable
    ): Page<OutputProjectFile>

    @ApiOperation("Specify description for a file")
    @PutMapping("/{fileId}/description", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setDescriptionToFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long,
        @Valid @RequestBody projectFileDescription: InputProjectFileDescription
    ): OutputProjectFile

    @ApiOperation("Delete existing file")
    @DeleteMapping("/{fileId}")
    fun deleteFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long
    )

}
