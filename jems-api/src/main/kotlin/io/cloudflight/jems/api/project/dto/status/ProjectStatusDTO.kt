package io.cloudflight.jems.api.project.dto.status

import io.cloudflight.jems.api.user.dto.OutputUser
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectStatusDTO(
    val id: Long?,
    val status: ApplicationStatusDTO,
    val user: OutputUser,
    val updated: ZonedDateTime,
    val decisionDate: LocalDate? = null,
    val entryIntoForceDate: LocalDate? = null,
    val note: String? = null
)
