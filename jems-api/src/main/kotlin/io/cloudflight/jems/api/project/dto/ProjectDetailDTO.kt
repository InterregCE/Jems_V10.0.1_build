package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.status.OutputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.OutputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.api.user.dto.OutputUser

data class ProjectDetailDTO(
    val id: Long?,
    val callSettings: ProjectCallSettingsDTO,
    val acronym: String,
    val applicant: OutputUser,
    val projectStatus: ProjectStatusDTO,
    val firstSubmission: ProjectStatusDTO? = null,
    val lastResubmission: ProjectStatusDTO? = null,
    val qualityAssessment: OutputProjectQualityAssessment? = null,
    val eligibilityAssessment: OutputProjectEligibilityAssessment? = null,
    val eligibilityDecision: ProjectStatusDTO? = null,
    val fundingDecision: ProjectStatusDTO? = null,
    val projectData: ProjectDataDTO? = null,
    val periods: List<ProjectPeriodDTO> = emptyList()
)
