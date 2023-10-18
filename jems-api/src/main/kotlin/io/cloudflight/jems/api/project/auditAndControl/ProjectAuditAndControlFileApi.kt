package io.cloudflight.jems.api.project.auditAndControl

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.auditAndControl.ProjectAuditAndControlApi.Companion.ENDPOINT_API_PROJECT_AUDIT_CONTROL
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

@Api("Project Audit and Control File")
interface ProjectAuditAndControlFileApi {
    companion object {
        private const val ENDPOINT_API_PROJECT_AUDIT_CONTROL_FILE = "$ENDPOINT_API_PROJECT_AUDIT_CONTROL/{auditControlId}/file/"
    }

    @ApiOperation("List files for Project Audit and Control")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_PROJECT_AUDIT_CONTROL_FILE)
    fun list(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        pageable: Pageable,
    ): Page<JemsFileDTO>

    @ApiOperation("Upload file to Project Audit and Control")
    @PostMapping(
        ENDPOINT_API_PROJECT_AUDIT_CONTROL_FILE,
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun upload(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @RequestPart("file") file: MultipartFile,
    ): JemsFileMetadataDTO

    @ApiOperation("Update description for Project Audit and Control")
    @PutMapping(
        "${ENDPOINT_API_PROJECT_AUDIT_CONTROL_FILE}/byFileId/{fileId}/description",
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateDescription(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable fileId: Long,
        @RequestBody(required = false) description: String?,
    )

    @ApiOperation("Download file from Project Audit and Control")
    @GetMapping(
        "$ENDPOINT_API_PROJECT_AUDIT_CONTROL_FILE/byFileId/{fileId}/",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun download(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable fileId: Long,
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Delete file from Project Audit and Control")
    @DeleteMapping(
        "$ENDPOINT_API_PROJECT_AUDIT_CONTROL_FILE/byFileId/{fileId}/",
    )
    fun delete(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable fileId: Long,
    )
}
