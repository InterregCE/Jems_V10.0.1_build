package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import java.time.ZonedDateTime

data class ProjectSearchRequest(
    val id: String? = null,
    val acronym: String? = null,
    val firstSubmissionFrom: ZonedDateTime? = null,
    val firstSubmissionTo: ZonedDateTime? = null,
    val lastSubmissionFrom: ZonedDateTime? = null,
    val lastSubmissionTo: ZonedDateTime? = null,
    val objectives: Set<ProgrammeObjectivePolicy>? = null,
    val statuses: Set<ApplicationStatus>? = null,
    val calls: Set<Long>? = null,
    val users: Set<Long>? = null
)
