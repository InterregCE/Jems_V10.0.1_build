package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment

data class ProjectDecision(
    val qualityAssessment: OutputProjectQualityAssessment? = null,
    val eligibilityAssessment: OutputProjectEligibilityAssessment? = null,
    val eligibilityDecision: ProjectStatus? = null,
    val fundingDecision: ProjectStatus? = null
)
