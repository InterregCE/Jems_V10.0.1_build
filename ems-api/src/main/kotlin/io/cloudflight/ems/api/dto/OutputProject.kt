package io.cloudflight.ems.api.dto

import io.cloudflight.ems.api.dto.user.OutputUser

data class OutputProject (
    val id: Long?,
    val acronym: String,
    val applicant: OutputUser,
    val projectStatus: OutputProjectStatus,
    val firstSubmission: OutputProjectStatus? = null,
    val lastResubmission: OutputProjectStatus? = null,
    val qualityAssessment: OutputProjectQualityAssessment? = null,
    val eligibilityAssessment: OutputProjectEligibilityAssessment? = null,
    val eligibilityDecision: OutputProjectStatus? = null,
    val fundingDecision: OutputProjectStatus? = null
)
