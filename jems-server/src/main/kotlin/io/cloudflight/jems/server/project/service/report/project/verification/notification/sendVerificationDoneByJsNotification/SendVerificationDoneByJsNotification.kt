package io.cloudflight.jems.server.project.service.report.project.verification.notification.sendVerificationDoneByJsNotification

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.notification.handler.ProjectReportDoneByJs
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationExpenditure
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification.ReportVerificationNotStartedException
import io.cloudflight.jems.server.project.service.report.project.projectReportVerificationDoneByJs
import io.cloudflight.jems.server.project.service.report.project.verification.notification.ProjectReportVerificationNotificationPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class SendVerificationDoneByJsNotification(
    private val persistence: ProjectReportVerificationNotificationPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService,
    private val projectReportPersistence: ProjectReportPersistence,
    private val callNotificationConfigurationsPersistence: CallNotificationConfigurationsPersistence,
    private val projectPersistence: ProjectPersistence
) : SendVerificationDoneByJsNotificationInteractor {

    @CanEditReportVerificationExpenditure
    @Transactional
    @ExceptionWrapper(SendVerificationDoneByJsNotificationException::class)
    override fun sendVerificationDoneByJsNotification(
        projectId: Long,
        reportId: Long
    ): ProjectReportVerificationNotification {
        val projectReport = projectReportPersistence.getReportById(projectId, reportId)
        validateReportIsInVerification(projectReport)
        validateNotificationIsToggled(projectId)
        val currentUser = securityService.currentUser?.user!!
        val currentTime = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)

        return persistence.storeVerificationNotificationMetaData(reportId, currentUser.id, currentTime).also {
            auditPublisher.publishEvent(ProjectReportDoneByJs(this, projectReport))
            auditPublisher.publishEvent(
                projectReportVerificationDoneByJs(
                    context = this,
                    projectId = projectId,
                    report = projectReport
                )
            )
        }
    }

    private fun validateReportIsInVerification(projectReport: ProjectReportModel) {
        if (!projectReport.status.canBeVerified())
            throw ReportVerificationNotStartedException()
    }

    private fun validateNotificationIsToggled(projectId: Long) {
        val callId = projectPersistence.getCallIdOfProject(projectId)

        if (callNotificationConfigurationsPersistence
                .getActiveNotificationOfType(
                    callId,
                    NotificationType.ProjectReportVerificationDoneNotificationSent
                ) == null
        )
            throw VerificationNotificationNotEnabledInCallException()
    }

}
