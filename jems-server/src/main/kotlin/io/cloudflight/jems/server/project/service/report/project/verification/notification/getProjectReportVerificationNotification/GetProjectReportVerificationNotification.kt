package io.cloudflight.jems.server.project.service.report.project.verification.notification.getProjectReportVerificationNotification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationExpenditure
import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification
import io.cloudflight.jems.server.project.service.report.project.verification.notification.ProjectReportVerificationNotificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportVerificationNotification(
    private val projectReportVerificationNotificationPersistence: ProjectReportVerificationNotificationPersistence
): GetProjectReportVerificationNotificationInteractor {

    @CanViewReportVerificationExpenditure
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportVerificationNotificationException::class)
    override fun getLastVerificationNotification(projectId: Long, reportId: Long): ProjectReportVerificationNotification? =
        projectReportVerificationNotificationPersistence.getLastVerificationNotificationMetaData(reportId)

}
