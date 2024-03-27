package io.cloudflight.jems.server.project.repository.report.project.verification.notification

import io.cloudflight.jems.server.project.entity.report.verification.notification.ProjectReportVerificationNotificationEntity
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.report.model.project.verification.notification.ProjectReportVerificationNotification
import io.cloudflight.jems.server.project.service.report.project.verification.notification.ProjectReportVerificationNotificationPersistence
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class ProjectReportVerificationNotificationPersistenceProvider(
    private val repository: ProjectReportVerificationNotificationRepository,
    private val userRepository: UserRepository,
    private val projectReportRepository: ProjectReportRepository,
) : ProjectReportVerificationNotificationPersistence {

    @Transactional(readOnly = true)
    override fun getLastVerificationNotificationMetaData(reportId: Long): ProjectReportVerificationNotification? =
        repository.findTop1ByProjectReportIdOrderByIdDesc(reportId)?.toModel()


    @Transactional
    override fun storeVerificationNotificationMetaData(
        reportId: Long,
        userId: Long,
        timeUtc: LocalDateTime
    ): ProjectReportVerificationNotification =
        repository.save(getNewNotificationEntity(userId, reportId, timeUtc))
            .toModel()

    private fun getNewNotificationEntity(userId: Long, reportId: Long, timeUtc: LocalDateTime) =
        ProjectReportVerificationNotificationEntity(
            id = 0,
            user = userRepository.getReferenceById(userId),
            projectReport = projectReportRepository.getReferenceById(reportId),
            createdAt = timeUtc
        )

}
