package io.cloudflight.jems.api.project.auditAndControl.corrections

import io.cloudflight.jems.api.project.auditAndControl.ProjectAuditAndControlApi
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionLineDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Correction")
interface ProjectAuditControlCorrectionApi {

    companion object {
        const val ENDPOINT_API_PROJECT_CORRECTION =
            "${ProjectAuditAndControlApi.ENDPOINT_API_PROJECT_AUDIT_CONTROL}/{auditControlId}/corrections"
    }

    @ApiOperation("Create new project audit/control correction")
    @PostMapping(ENDPOINT_API_PROJECT_CORRECTION)
    fun createProjectAuditCorrection(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @RequestParam type: AuditControlCorrectionTypeDTO,
    ): ProjectAuditControlCorrectionDTO

    @ApiOperation("List all corrections in an audit/control")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping(ENDPOINT_API_PROJECT_CORRECTION)
    fun listProjectAuditCorrections(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        pageable: Pageable,
    ): Page<ProjectAuditControlCorrectionLineDTO>

    @ApiOperation("Get correction for an audit/control")
    @GetMapping("$ENDPOINT_API_PROJECT_CORRECTION/{correctionId}")
    fun getProjectAuditCorrection(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
    ): ProjectAuditControlCorrectionDTO

    @ApiOperation("Delete last correction from audit/control")
    @DeleteMapping("$ENDPOINT_API_PROJECT_CORRECTION/{correctionId}")
    fun deleteProjectAuditCorrection(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
    )

    @ApiOperation("Close correction")
    @PostMapping("$ENDPOINT_API_PROJECT_CORRECTION/{correctionId}/close")
    fun closeProjectCorrection(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
    ): AuditStatusDTO

    @ApiOperation("Update project audit/control correction identification")
    @PutMapping("$ENDPOINT_API_PROJECT_CORRECTION/{correctionId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCorrectionIdentification(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
        @RequestBody correctionIdentificationUpdate: ProjectCorrectionIdentificationUpdateDTO,
    ): ProjectAuditControlCorrectionDTO

    @ApiOperation("Get Project Past corrections")
    @GetMapping("$ENDPOINT_API_PROJECT_CORRECTION/{correctionId}/previousClosedCorrections")
    fun getPreviousClosedCorrections(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
    ): List<AuditControlCorrectionDTO>

}
