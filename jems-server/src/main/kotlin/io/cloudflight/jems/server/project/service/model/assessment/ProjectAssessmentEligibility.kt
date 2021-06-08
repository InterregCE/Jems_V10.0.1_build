package io.cloudflight.jems.server.project.service.model.assessment

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import java.time.ZonedDateTime

data class ProjectAssessmentEligibility(
    val projectId: Long,
    val step: Int,
    val result: ProjectAssessmentEligibilityResult,
    val updated: ZonedDateTime? = null,
    val note: String? = null,
)
