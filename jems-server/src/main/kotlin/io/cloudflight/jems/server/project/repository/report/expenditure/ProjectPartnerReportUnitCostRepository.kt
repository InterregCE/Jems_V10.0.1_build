package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportUnitCostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportUnitCostRepository : JpaRepository<PartnerReportUnitCostEntity, Long> {
    fun findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(
        partnerId: Long,
        reportId: Long,
    ): MutableList<PartnerReportUnitCostEntity>
}
