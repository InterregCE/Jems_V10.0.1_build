package io.cloudflight.ems.api.project.dto.status

import io.cloudflight.ems.api.user.dto.OutputUser
import java.time.ZonedDateTime

data class OutputProjectQualityAssessment(
    val result: ProjectQualityAssessmentResult,
    val user: OutputUser,
    val updated: ZonedDateTime,
    val note: String? = null
)
