package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import java.time.ZonedDateTime

data class ProjectSearchRequest(
    val id: String?,
    val acronym: String?,
    val firstSubmissionFrom: ZonedDateTime?,
    val firstSubmissionTo: ZonedDateTime?,
    val lastSubmissionFrom: ZonedDateTime?,
    val lastSubmissionTo: ZonedDateTime?,
    val objectives: Set<ProgrammeObjectivePolicy>?,
    val statuses: Set<ApplicationStatus>?,
    val calls: Set<Long>?,
)
