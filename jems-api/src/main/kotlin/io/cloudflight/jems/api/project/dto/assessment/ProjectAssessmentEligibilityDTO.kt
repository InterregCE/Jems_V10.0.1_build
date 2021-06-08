package io.cloudflight.jems.api.project.dto.assessment

data class ProjectAssessmentEligibilityDTO(
    val result: ProjectAssessmentEligibilityResult,
    val note: String? = null,
)
