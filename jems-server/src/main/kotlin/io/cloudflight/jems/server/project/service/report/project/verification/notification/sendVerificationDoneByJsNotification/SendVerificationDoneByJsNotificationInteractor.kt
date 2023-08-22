package io.cloudflight.jems.server.project.service.report.project.verification.notification.sendVerificationDoneByJsNotification

import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification

interface SendVerificationDoneByJsNotificationInteractor {

    fun sendVerificationDoneByJsNotification(projectId: Long, reportId: Long): ProjectReportVerificationNotification
}
