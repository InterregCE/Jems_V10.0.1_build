package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportExpenditureRepository : JpaRepository<PartnerReportExpenditureCostEntity, Long> {

    fun findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
        reportId: Long,
        partnerId: Long,
    ): MutableList<PartnerReportExpenditureCostEntity>

    @Query("SELECT e.id FROM #{#entityName} e where e.partnerReport=:reportEntity")
    fun findExistingExpenditureIdsFor(reportEntity: ProjectPartnerReportEntity): Set<Long>

}
