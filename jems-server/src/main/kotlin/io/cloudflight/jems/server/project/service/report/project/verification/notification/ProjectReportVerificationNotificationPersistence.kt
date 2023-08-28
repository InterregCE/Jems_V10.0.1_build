package io.cloudflight.jems.server.project.service.report.project.verification.notification

import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification
import java.time.LocalDateTime

interface ProjectReportVerificationNotificationPersistence {

    fun getLastVerificationNotificationMetaData(reportId: Long): ProjectReportVerificationNotification?

    fun storeVerificationNotificationMetaData(
        reportId: Long,
        userId: Long,
        timeUtc: LocalDateTime,
    ): ProjectReportVerificationNotification

}
