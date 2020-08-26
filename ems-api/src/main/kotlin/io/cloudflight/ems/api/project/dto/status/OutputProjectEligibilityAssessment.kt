package io.cloudflight.ems.api.project.dto.status

import io.cloudflight.ems.api.user.dto.OutputUser
import java.time.ZonedDateTime

data class OutputProjectEligibilityAssessment(
    val result: ProjectEligibilityAssessmentResult,
    val user: OutputUser,
    val updated: ZonedDateTime,
    val note: String? = null
)
