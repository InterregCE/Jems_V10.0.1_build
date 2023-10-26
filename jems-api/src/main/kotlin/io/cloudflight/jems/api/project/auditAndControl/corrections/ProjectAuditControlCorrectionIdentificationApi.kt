package io.cloudflight.jems.api.project.auditAndControl.corrections

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectAuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionIdentificationUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Correction Identification")
interface ProjectAuditControlCorrectionIdentificationApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_CORRECTION_IDENTIFICATION =
            "${ProjectAuditControlCorrectionApi.ENDPOINT_API_PROJECT_CORRECTION}/{correctionId}/identification"
    }

    @ApiOperation("Update project audit/control correction identification")
    @PutMapping(ENDPOINT_API_PROJECT_CORRECTION_IDENTIFICATION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCorrectionIdentification(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
        @RequestBody correctionIdentificationUpdate: ProjectCorrectionIdentificationUpdateDTO
    ): ProjectCorrectionIdentificationDTO

    @ApiOperation("Get Project Audit/Control Correction Identification")
    @GetMapping(ENDPOINT_API_PROJECT_CORRECTION_IDENTIFICATION)
    fun getCorrectionIdentification(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
    ): ProjectCorrectionIdentificationDTO

    @ApiOperation("Get Project Past corrections")
    @GetMapping("$ENDPOINT_API_PROJECT_CORRECTION_IDENTIFICATION/previousClosedCorrections")
    fun getPreviousClosedCorrections(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
    ): List<ProjectAuditControlCorrectionDTO>

}
