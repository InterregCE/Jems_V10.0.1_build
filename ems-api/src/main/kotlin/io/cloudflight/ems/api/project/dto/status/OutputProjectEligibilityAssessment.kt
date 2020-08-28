package io.cloudflight.ems.api.project.dto.status

import java.time.ZonedDateTime

data class OutputProjectEligibilityAssessment(
    val result: ProjectEligibilityAssessmentResult,
    val updated: ZonedDateTime,
    val note: String? = null
)
