package io.cloudflight.jems.api.project.auditAndControl.corrections.impact

import io.cloudflight.jems.api.project.auditAndControl.corrections.ProjectAuditControlCorrectionApi
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.AuditControlCorrectionImpactDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Audit Control Correction Impact")
interface AuditControlCorrectionImpactApi {

    companion object {
        private const val ENDPOINT_API_CORRECTION_IMPACT =
            "${ProjectAuditControlCorrectionApi.ENDPOINT_API_PROJECT_CORRECTION}/{correctionId}/impact"
    }

    @ApiOperation("Update impact of correction")
    @PutMapping(ENDPOINT_API_CORRECTION_IMPACT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateImpact(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
        @RequestBody impact: AuditControlCorrectionImpactDTO,
    ): AuditControlCorrectionImpactDTO

}
