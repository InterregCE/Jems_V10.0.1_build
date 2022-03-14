package io.cloudflight.jems.server.project.repository.report.expenditureCosts

import io.cloudflight.jems.server.project.entity.report.expenditureCosts.PartnerReportExpenditureCostEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PartnerReportExpenditureCostsRepository : JpaRepository<PartnerReportExpenditureCostEntity, Long> {

    fun findAllByPartnerReportIdOrderById(reportEntityId: Long): MutableList<PartnerReportExpenditureCostEntity>
}
