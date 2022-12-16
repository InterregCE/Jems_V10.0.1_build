package io.cloudflight.jems.server.project.repository.report.partner.control.identification

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportVerificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProjectPartnerReportVerificationRepository :
    JpaRepository<ProjectPartnerReportVerificationEntity, ProjectPartnerReportEntity> {
    fun findByReportEntityIdAndReportEntityPartnerId(
        reportId: Long,
        partnerId: Long,
    ): Optional<ProjectPartnerReportVerificationEntity>
}
