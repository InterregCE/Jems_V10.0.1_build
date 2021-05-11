package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.user.service.model.UserSummary

data class Project(
    val id: Long?,
    val callSettings: ProjectCallSettings,
    val acronym: String,
    val applicant: UserSummary,
    val projectStatus: ProjectStatus,
    val firstSubmission: ProjectStatus? = null,
    val lastResubmission: ProjectStatus? = null,
    val step2Active: Boolean,
    val firstStepDecision: ProjectDecision? = null,
    val secondStepDecision: ProjectDecision? = null,

    // projectData
    val title: Set<InputTranslation>? = emptySet(),
    val intro: Set<InputTranslation>? = emptySet(),
    val duration: Int?,
    val specificObjective: OutputProgrammePriorityPolicySimpleDTO?,
    val programmePriority: OutputProgrammePrioritySimple?,

    val periods: List<ProjectPeriod> = emptyList()
)  {
    fun getDecision(): ProjectDecision? {
        return if (step2Active) {
            secondStepDecision
        } else {
            firstStepDecision
        }
    }
}
