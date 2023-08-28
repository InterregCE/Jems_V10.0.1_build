package io.cloudflight.jems.server.project.service.report.project.verification.notification.getProjectReportVerificationNotification

import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification

interface GetProjectReportVerificationNotificationInteractor {

    fun getLastVerificationNotification(projectId: Long, reportId: Long): ProjectReportVerificationNotification?
}
