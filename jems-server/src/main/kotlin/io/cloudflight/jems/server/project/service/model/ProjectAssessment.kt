package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality

data class ProjectAssessment(
    val assessmentQuality: ProjectAssessmentQuality? = null,
    val assessmentEligibility: ProjectAssessmentEligibility? = null,
    val eligibilityDecision: ProjectStatus? = null,
    val preFundingDecision: ProjectStatus? = null,
    val fundingDecision: ProjectStatus? = null
)
