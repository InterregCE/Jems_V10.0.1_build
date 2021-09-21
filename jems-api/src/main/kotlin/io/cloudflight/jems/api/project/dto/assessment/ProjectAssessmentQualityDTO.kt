package io.cloudflight.jems.api.project.dto.assessment

data class ProjectAssessmentQualityDTO(
    val result: ProjectAssessmentQualityResult,
    val note: String? = null,
)
