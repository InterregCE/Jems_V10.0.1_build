package io.cloudflight.ems.api.project.dto.status

import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.api.project.dto.status.ProjectEligibilityAssessmentResult
import java.time.ZonedDateTime

data class OutputProjectEligibilityAssessment(
    val result: ProjectEligibilityAssessmentResult,
    val user: OutputUser,
    val updated: ZonedDateTime,
    val note: String? = null
)
