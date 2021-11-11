package io.cloudflight.jems.api.project.dto.status

data class ProjectDecisionDTO(
    val qualityAssessment: OutputProjectQualityAssessment? = null,
    val eligibilityAssessment: OutputProjectEligibilityAssessment? = null,
    val eligibilityDecision: ProjectStatusDTO? = null,
    val preFundingDecision: ProjectStatusDTO? = null,
    val finalFundingDecision: ProjectStatusDTO? = null,
    val modificationDecision: ProjectStatusDTO? = null,
)
