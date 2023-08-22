package io.cloudflight.jems.server.project.controller.report.project.verification.notification

import io.cloudflight.jems.api.project.report.project.verification.ProjectReportVerificationNotificationApi
import io.cloudflight.jems.server.project.service.report.project.verification.notification.sendVerificationDoneByJsNotification.SendVerificationDoneByJsNotificationInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.notification.getProjectReportVerificationNotification.GetProjectReportVerificationNotificationInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectReportVerificationNotificationController(
    private val getProjectReportVerificationNotificationInteractor: GetProjectReportVerificationNotificationInteractor,
    private val sendProjectReportVerificationNotificationInteractor: SendVerificationDoneByJsNotificationInteractor
): ProjectReportVerificationNotificationApi {

    override fun sendVerificationDoneByJsNotification(projectId: Long, reportId: Long) =
        sendProjectReportVerificationNotificationInteractor.sendVerificationDoneByJsNotification(projectId, reportId).toDto()


    override fun getLastProjectReportVerificationNotification(projectId: Long, reportId: Long) =
        getProjectReportVerificationNotificationInteractor.getLastVerificationNotification(projectId, reportId)?.toDto()

}
