package io.cloudflight.jems.api.project.dto.status

import java.time.ZonedDateTime

data class OutputProjectQualityAssessment(
    val result: ProjectQualityAssessmentResult,
    val updated: ZonedDateTime,
    val note: String? = null
)
