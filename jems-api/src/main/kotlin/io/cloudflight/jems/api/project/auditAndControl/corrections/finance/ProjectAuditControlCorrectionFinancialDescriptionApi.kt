package io.cloudflight.jems.api.project.auditAndControl.corrections.finance

import io.cloudflight.jems.api.project.auditAndControl.corrections.ProjectAuditControlCorrectionApi
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.finance.ProjectCorrectionFinancialDescriptionUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Correction Financial Description")
interface ProjectAuditControlCorrectionFinancialDescriptionApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION =
            "${ProjectAuditControlCorrectionApi.ENDPOINT_API_PROJECT_CORRECTION}/{correctionId}/financialDescription"
    }

    @ApiOperation("Get financial description section of correction")
    @GetMapping("$ENDPOINT_API_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION")
    fun getCorrectionFinancialDescription(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
    ): ProjectCorrectionFinancialDescriptionDTO

    @ApiOperation("Update financial description section of correction")
    @PutMapping("$ENDPOINT_API_PROJECT_CORRECTION_FINANCIAL_DESCRIPTION", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCorrectionFinancialDescription(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
        @RequestBody financialDescriptionUpdate: ProjectCorrectionFinancialDescriptionUpdateDTO
    ): ProjectCorrectionFinancialDescriptionDTO
}
