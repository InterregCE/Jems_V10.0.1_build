package io.cloudflight.jems.server.project.repository.report.contribution

import io.cloudflight.jems.server.project.entity.report.contribution.ProjectPartnerReportContributionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportContributionRepository :
    JpaRepository<ProjectPartnerReportContributionEntity, Long> {

    fun findAllByReportEntityIdAndReportEntityPartnerIdOrderById(
        reportId: Long,
        partnerId: Long,
    ): List<ProjectPartnerReportContributionEntity>

    fun findAllByReportEntityIdInOrderByReportEntityIdAscIdAsc(
        reportIds: Set<Long>,
    ): List<ProjectPartnerReportContributionEntity>

    fun existsByReportEntityPartnerIdAndReportEntityIdAndId(partnerId: Long, reportId: Long, contribId: Long): Boolean

}
