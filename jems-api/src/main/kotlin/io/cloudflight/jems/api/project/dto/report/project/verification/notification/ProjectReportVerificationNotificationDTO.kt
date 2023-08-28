package io.cloudflight.jems.api.project.dto.report.project.verification.notification

import io.cloudflight.jems.api.common.dto.file.UserSimpleDTO
import java.time.ZonedDateTime

data class ProjectReportVerificationNotificationDTO(
    val reportId: Long,
    val triggeredByUser: UserSimpleDTO,
    val createdAt: ZonedDateTime,
)
