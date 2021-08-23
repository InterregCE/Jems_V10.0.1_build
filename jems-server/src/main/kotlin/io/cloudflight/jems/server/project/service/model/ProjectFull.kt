package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.user.service.model.UserSummary

data class ProjectFull(
    val id: Long?,
    val customIdentifier: String,
    val callSettings: ProjectCallSettings,
    val acronym: String,
    val applicant: UserSummary,
    val projectStatus: ProjectStatus,
    val firstSubmission: ProjectStatus? = null,
    val lastResubmission: ProjectStatus? = null,
    var assessmentStep1: ProjectAssessment? = null,
    var assessmentStep2: ProjectAssessment? = null,

    // projectData
    val title: Set<InputTranslation>? = emptySet(),
    val intro: Set<InputTranslation>? = emptySet(),
    val duration: Int?,
    val specificObjective: OutputProgrammePriorityPolicySimpleDTO? = null,
    val programmePriority: OutputProgrammePrioritySimple? = null,

    val periods: List<ProjectPeriod> = emptyList()
) {
    fun isInStep2() = projectStatus.status.isInStep2()
}
