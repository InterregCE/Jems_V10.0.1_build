package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePriorityPolicySimpleDTO
import io.cloudflight.jems.api.programme.dto.priority.OutputProgrammePrioritySimple
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.user.service.model.UserSummary
import java.time.LocalDate

data class ProjectDetail(
    val id: Long?,
    val customIdentifier: String,
    val callSettings: ProjectCallSettings,
    val acronym: String,
    val applicant: UserSummary,
    val title: Set<InputTranslation> = emptySet(),
    val specificObjective: OutputProgrammePriorityPolicySimpleDTO?,
    val programmePriority: OutputProgrammePrioritySimple?,

    val projectStatus: ProjectStatus,
    val firstSubmission: ProjectStatus? = null,
    val firstSubmissionStep1: ProjectStatus? = null,
    val lastResubmission: ProjectStatus? = null,
    val contractedDecision: ProjectStatus? = null,
    val contractedOnDate: LocalDate? = null,
    var assessmentStep1: ProjectAssessment? = null,
    var assessmentStep2: ProjectAssessment? = null,
) {
    fun isInStep2() = projectStatus.status.isInStep2()
}
