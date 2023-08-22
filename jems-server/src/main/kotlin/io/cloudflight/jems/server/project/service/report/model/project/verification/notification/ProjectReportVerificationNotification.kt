package io.cloudflight.jems.server.project.service.report.model.project.verification.notification

import io.cloudflight.jems.server.common.file.service.model.UserSimple
import java.time.ZonedDateTime

data class ProjectReportVerificationNotification(
    val id: Long,
    val reportId: Long,
    val triggeredByUser: UserSimple,
    val createdAt: ZonedDateTime,
)
