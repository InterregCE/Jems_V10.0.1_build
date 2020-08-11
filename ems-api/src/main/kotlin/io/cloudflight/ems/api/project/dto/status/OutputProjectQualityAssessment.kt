package io.cloudflight.ems.api.project.dto.status

import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.api.project.dto.status.ProjectQualityAssessmentResult
import java.time.ZonedDateTime

data class OutputProjectQualityAssessment(
    val result: ProjectQualityAssessmentResult,
    val user: OutputUser,
    val updated: ZonedDateTime,
    val note: String? = null
)
