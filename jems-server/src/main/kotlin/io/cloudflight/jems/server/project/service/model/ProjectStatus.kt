package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.service.model.UserSummary
import java.time.LocalDate
import java.time.ZonedDateTime

data class ProjectStatus(
    val id: Long? = null,
    val status: ApplicationStatus,
    val user: UserSummary,
    val updated: ZonedDateTime,
    val decisionDate: LocalDate? = null,
    val note: String? = null
)
