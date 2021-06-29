package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.file.InputProjectFileDescription
import io.cloudflight.jems.api.project.dto.file.OutputProjectFile
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
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import javax.validation.Valid

@Api("Project File Storage")
@RequestMapping("/api/project/{projectId}/file")
interface ProjectFileApi {

    @ApiOperation("Upload assessment file to project")
    @PostMapping("/assessment", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProjectAssessmentFile(
        @PathVariable projectId: Long,
        @RequestPart("file") file: MultipartFile
    )

    @ApiOperation("Download project assessment file")
    @GetMapping("/assessment/{fileId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadProjectAssessmentFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Get list of project assessment files")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer")
    )
    @GetMapping("/assessment")
    fun getAssessmentFilesForProject(
        @PathVariable projectId: Long,
        pageable: Pageable
    ): Page<OutputProjectFile>

    @ApiOperation("Specify description for project assessment file")
    @PutMapping("/assessment/{fileId}/description", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setDescriptionToProjectAssessmentFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long,
        @Valid @RequestBody projectFileDescription: InputProjectFileDescription
    ): OutputProjectFile

    @ApiOperation("Delete existing file")
    @DeleteMapping("/assessment/{fileId}")
    fun deleteProjectAssessmentFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long
    )


    @ApiOperation("Upload application file to project")
    @PostMapping("/applicant", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProjectApplicationFile(
        @PathVariable projectId: Long,
        @RequestPart("file") file: MultipartFile
    )

    @ApiOperation("Download project application file")
    @GetMapping("/applicant/{fileId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadProjectApplicationFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Get list of project application files")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer")
    )
    @GetMapping("/applicant")
    fun getApplicationFilesForProject(
        @PathVariable projectId: Long,
        pageable: Pageable
    ): Page<OutputProjectFile>

    @ApiOperation("Specify description for project application file")
    @PutMapping("/applicant/{fileId}/description", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun setDescriptionToProjectApplicationFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long,
        @Valid @RequestBody projectFileDescription: InputProjectFileDescription
    ): OutputProjectFile

    @ApiOperation("Delete existing file")
    @DeleteMapping("/applicant/{fileId}")
    fun deleteProjectApplicationFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long
    )

}
