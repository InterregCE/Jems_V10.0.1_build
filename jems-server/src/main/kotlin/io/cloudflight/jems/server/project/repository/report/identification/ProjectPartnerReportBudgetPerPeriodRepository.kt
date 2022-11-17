package io.cloudflight.jems.server.project.repository.report.identification

import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportBudgetPerPeriodEntity
import io.cloudflight.jems.server.project.entity.report.identification.ProjectPartnerReportBudgetPerPeriodId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerReportBudgetPerPeriodRepository :
    JpaRepository<ProjectPartnerReportBudgetPerPeriodEntity, ProjectPartnerReportBudgetPerPeriodId> {

    fun findAllByIdReportPartnerIdAndIdReportIdOrderByIdPeriodNumber(
        partnerId: Long,
        reportId: Long,
    ): MutableList<ProjectPartnerReportBudgetPerPeriodEntity>

    fun findByIdReportIdAndIdPeriodNumber(reportId: Long, periodNumber: Int): ProjectPartnerReportBudgetPerPeriodEntity

}
