package io.cloudflight.jems.server.project.entity.assessment

import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity

data class ProjectAssessmentEntity(
    val assessmentQuality: ProjectAssessmentQualityEntity? = null,
    val assessmentEligibility: ProjectAssessmentEligibilityEntity? = null,
    val eligibilityDecision: ProjectStatusHistoryEntity? = null,
    val preFundingDecision: ProjectStatusHistoryEntity? = null,
    val fundingDecision: ProjectStatusHistoryEntity? = null
) {
    fun getOrNull(): ProjectAssessmentEntity? {
        if (assessmentQuality == null
            && assessmentEligibility == null
            && eligibilityDecision == null
            && preFundingDecision == null
            && fundingDecision == null
        )
            return null
        else
            return this
    }
}

