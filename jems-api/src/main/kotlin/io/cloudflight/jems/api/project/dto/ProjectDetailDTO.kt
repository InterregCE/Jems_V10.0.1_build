package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.project.dto.status.ProjectDecisionDTO
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.api.user.dto.OutputUser

data class ProjectDetailDTO(
    val id: Long?,
    val customIdentifier: String,
    val callSettings: ProjectCallSettingsDTO,
    val acronym: String,
    val applicant: OutputUser,
    val title: Set<InputTranslation> = emptySet(),
    val specificObjective: OutputProgrammePriorityPolicySimpleDTO?,
    val programmePriority: OutputProgrammePrioritySimple?,

    val projectStatus: ProjectStatusDTO,
    val firstSubmission: ProjectStatusDTO? = null,
    val firstSubmissionStep1: ProjectStatusDTO? = null,
    val lastResubmission: ProjectStatusDTO? = null,
    val step2Active: Boolean,
    val firstStepDecision: ProjectDecisionDTO? = null,
    val secondStepDecision: ProjectDecisionDTO? = null,
    val contractedDecision: ProjectStatusDTO? = null
)
