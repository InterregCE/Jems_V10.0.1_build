package io.cloudflight.ems.api.project.dto.status

import io.cloudflight.ems.api.user.dto.OutputUser
import java.time.LocalDate
import java.time.ZonedDateTime

data class OutputProjectStatus(
    val id: Long?,
    val status: ProjectApplicationStatus,
    val user: OutputUser,
    val updated: ZonedDateTime,
    val decisionDate: LocalDate? = null,
    val note: String? = null
)
