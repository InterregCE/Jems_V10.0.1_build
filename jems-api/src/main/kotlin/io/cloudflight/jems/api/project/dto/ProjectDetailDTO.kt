package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.project.dto.status.ProjectDecisionDTO
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
    val step2Active: Boolean,
    val firstStepDecision: ProjectDecisionDTO? = null,
    val secondStepDecision: ProjectDecisionDTO? = null,
    val projectData: ProjectDataDTO? = null,
    val periods: List<ProjectPeriodDTO> = emptyList()
)
