package io.cloudflight.jems.api.project.dto

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import java.time.ZonedDateTime

data class ProjectSearchRequestDTO(
    val id: String?,
    val acronym: String?,
    val firstSubmissionFrom: ZonedDateTime?,
    val firstSubmissionTo: ZonedDateTime?,
    val lastSubmissionFrom: ZonedDateTime?,
    val lastSubmissionTo: ZonedDateTime?,
    val objectives: Set<ProgrammeObjectivePolicy>?,
    val statuses: Set<ApplicationStatusDTO>?,
    val calls: Set<Long>?
)
