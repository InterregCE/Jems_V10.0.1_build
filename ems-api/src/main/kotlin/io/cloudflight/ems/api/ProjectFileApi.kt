package io.cloudflight.ems.api

import io.cloudflight.ems.api.dto.InputProjectFileDescription
import io.cloudflight.ems.api.dto.OutputProjectFile
import io.swagger.annotations.Api
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

@Api("Project File Storage")
@RequestMapping("/api/project/{projectId}/file")
interface ProjectFileApi {

    // TODO recheck if not possible to get swagger-codegen working without hidden
    @ApiOperation("Upload file to project", hidden = true)
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProjectFile(
        @PathVariable projectId: Long,
        @RequestPart("file") file: MultipartFile)

    @ApiOperation("Download file")
    @GetMapping("/{fileId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long): ResponseEntity<ByteArrayResource>

    @ApiOperation("Get list of files")
    @GetMapping
    fun getFilesForProject(@PathVariable projectId: Long, pageable: Pageable): Page<OutputProjectFile>

    @ApiOperation("Specify descritpion for a file")
    @PutMapping("/{fileId}/description")
    fun setDescriptionToFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long,
        @RequestBody projectFileDescription: InputProjectFileDescription): OutputProjectFile

    @ApiOperation("Delete existing file")
    @DeleteMapping("/{fileId}")
    fun deleteFile(
        @PathVariable projectId: Long,
        @PathVariable fileId: Long)

}
