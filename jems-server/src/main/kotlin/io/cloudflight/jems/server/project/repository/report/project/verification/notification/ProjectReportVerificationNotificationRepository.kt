package io.cloudflight.jems.server.project.repository.report.project.verification.notification

import io.cloudflight.jems.server.project.entity.report.verification.notification.ProjectReportVerificationNotificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectReportVerificationNotificationRepository: JpaRepository<ProjectReportVerificationNotificationEntity, Long> {
    fun findTop1ByProjectReportIdOrderByIdDesc(reportId: Long): ProjectReportVerificationNotificationEntity?

}
