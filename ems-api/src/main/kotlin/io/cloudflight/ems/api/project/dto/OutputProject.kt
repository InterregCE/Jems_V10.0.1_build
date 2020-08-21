package io.cloudflight.ems.api.project.dto

import io.cloudflight.ems.api.call.dto.OutputCallWithDates
import io.cloudflight.ems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.ems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.ems.api.project.dto.status.OutputProjectStatus
import io.cloudflight.ems.api.dto.user.OutputUser

data class OutputProject (
    val id: Long?,
    val call: OutputCallWithDates,
    val acronym: String,
    val applicant: OutputUser,
    val projectStatus: OutputProjectStatus,
    val firstSubmission: OutputProjectStatus? = null,
    val lastResubmission: OutputProjectStatus? = null,
    val qualityAssessment: OutputProjectQualityAssessment? = null,
    val eligibilityAssessment: OutputProjectEligibilityAssessment? = null,
    val eligibilityDecision: OutputProjectStatus? = null,
    val fundingDecision: OutputProjectStatus? = null,
    val projectData: OutputProjectData? = null,
    val projectPartners: List<OutputProjectPartner>? = emptyList()
)
