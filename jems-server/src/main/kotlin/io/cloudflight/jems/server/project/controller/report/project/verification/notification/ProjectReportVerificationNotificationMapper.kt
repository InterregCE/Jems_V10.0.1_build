package io.cloudflight.jems.server.project.controller.report.project.verification.notification

import io.cloudflight.jems.api.project.dto.report.project.verification.notification.ProjectReportVerificationNotificationDTO
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification

fun ProjectReportVerificationNotification.toDto() = ProjectReportVerificationNotificationDTO(
    reportId = reportId,
    triggeredByUser = triggeredByUser.toDto(),
    createdAt = createdAt,
)
