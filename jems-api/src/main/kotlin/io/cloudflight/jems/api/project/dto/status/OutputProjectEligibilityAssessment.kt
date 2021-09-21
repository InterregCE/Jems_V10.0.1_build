package io.cloudflight.jems.api.project.dto.status

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import java.time.ZonedDateTime

data class OutputProjectEligibilityAssessment(
    val result: ProjectAssessmentEligibilityResult,
    val updated: ZonedDateTime,
    val note: String? = null
)
