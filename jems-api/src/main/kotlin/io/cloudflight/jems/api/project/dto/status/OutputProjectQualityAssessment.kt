package io.cloudflight.jems.api.project.dto.status

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import java.time.ZonedDateTime

data class OutputProjectQualityAssessment(
    val result: ProjectAssessmentQualityResult,
    val updated: ZonedDateTime,
    val note: String? = null
)
