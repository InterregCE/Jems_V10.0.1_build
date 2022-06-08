package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import java.time.ZonedDateTime

data class ProjectSearchRequestDTO(
    val id: String? = null,
    val acronym: String? = null,
    val firstSubmissionFrom: ZonedDateTime? = null,
    val firstSubmissionTo: ZonedDateTime? = null,
    val lastSubmissionFrom: ZonedDateTime? = null,
    val lastSubmissionTo: ZonedDateTime? = null,
    val objectives: Set<ProgrammeObjectivePolicy>? = null,
    val statuses: Set<ApplicationStatusDTO>? = null,
    val calls: Set<Long>? = null,
)
