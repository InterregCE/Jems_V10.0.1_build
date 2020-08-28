package io.cloudflight.ems.api.project.dto.status

import java.time.ZonedDateTime

data class OutputProjectQualityAssessment(
    val result: ProjectQualityAssessmentResult,
    val updated: ZonedDateTime,
    val note: String? = null
)
