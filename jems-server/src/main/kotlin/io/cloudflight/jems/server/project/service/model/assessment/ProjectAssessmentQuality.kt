package io.cloudflight.jems.server.project.service.model.assessment

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import java.time.ZonedDateTime

data class ProjectAssessmentQuality(
    val projectId: Long,
    val step: Int,
    val result: ProjectAssessmentQualityResult,
    val updated: ZonedDateTime? = null,
    val note: String? = null,
)
