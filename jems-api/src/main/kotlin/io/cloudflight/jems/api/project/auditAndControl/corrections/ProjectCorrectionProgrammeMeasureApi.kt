package io.cloudflight.jems.api.project.auditAndControl.corrections

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionProgrammeMeasureDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.ProjectCorrectionProgrammeMeasureUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Correction Programme Measure")
interface ProjectCorrectionProgrammeMeasureApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_CORRECTION_PROGRAMME_MEASURE =
            "${ProjectAuditControlCorrectionApi.ENDPOINT_API_PROJECT_CORRECTION}/{correctionId}/programmeMeasure"
    }

    @ApiOperation("Get correction programme measure")
    @GetMapping(ENDPOINT_API_PROJECT_CORRECTION_PROGRAMME_MEASURE)
    fun getProgrammeMeasure(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
    ): ProjectCorrectionProgrammeMeasureDTO

    @ApiOperation("Update correction programme measure")
    @PutMapping(ENDPOINT_API_PROJECT_CORRECTION_PROGRAMME_MEASURE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeMeasure(
        @PathVariable projectId: Long,
        @PathVariable auditControlId: Long,
        @PathVariable correctionId: Long,
        @RequestBody programmeMeasure: ProjectCorrectionProgrammeMeasureUpdateDTO
    ): ProjectCorrectionProgrammeMeasureDTO
}
