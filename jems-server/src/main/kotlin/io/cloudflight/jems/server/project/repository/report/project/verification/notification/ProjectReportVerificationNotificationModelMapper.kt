package io.cloudflight.jems.server.project.repository.report.project.verification.notification

import io.cloudflight.jems.server.common.file.service.toModel
import io.cloudflight.jems.server.project.entity.report.verification.notification.ProjectReportVerificationNotificationEntity
import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification
import java.time.ZoneOffset.UTC

fun ProjectReportVerificationNotificationEntity.toModel() =
    ProjectReportVerificationNotification(
        id = id,
        reportId = projectReport.id,
        triggeredByUser = user.toModel(),
        createdAt = createdAt.atOffset(UTC).toZonedDateTime(),
)
