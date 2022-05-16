package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportLumpSumEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportLumpSumRepository : JpaRepository<PartnerReportLumpSumEntity, Long> {
    fun findByReportEntityPartnerIdAndReportEntityIdOrderByPeriodAscIdAsc(
        partnerId: Long,
        reportId: Long,
    ): MutableList<PartnerReportLumpSumEntity>
}
