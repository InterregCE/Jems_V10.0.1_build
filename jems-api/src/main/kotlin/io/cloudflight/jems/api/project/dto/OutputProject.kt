package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectStatus
import io.cloudflight.jems.api.user.dto.OutputUser

data class OutputProject (
    val id: Long?,
    val callSettings: ProjectCallSettingsDTO,
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
    val periods: List<OutputProjectPeriod> = emptyList()
)
