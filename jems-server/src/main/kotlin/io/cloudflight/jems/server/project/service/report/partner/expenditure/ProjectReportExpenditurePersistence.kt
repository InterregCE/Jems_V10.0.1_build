package io.cloudflight.jems.server.project.service.report.partner.expenditure

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost

interface ProjectReportExpenditurePersistence {

    fun getPartnerReportExpenditureCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost>

    fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
    ): List<ProjectPartnerReportExpenditureCost>

    fun existsByExpenditureId(partnerId: Long, reportId: Long, expenditureId: Long): Boolean

    fun getAvailableLumpSums(partnerId: Long, reportId: Long): List<ProjectPartnerReportLumpSum>

    fun getAvailableUnitCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportUnitCost>

    fun getAvailableInvestments(partnerId: Long, reportId: Long): List<ProjectPartnerReportInvestment>

    fun getAvailableBudgetOptions(partnerId: Long, reportId: Long): ProjectPartnerBudgetOptions
}
