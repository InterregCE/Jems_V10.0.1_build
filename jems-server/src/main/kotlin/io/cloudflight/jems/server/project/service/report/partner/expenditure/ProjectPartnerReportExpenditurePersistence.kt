package io.cloudflight.jems.server.project.service.report.partner.expenditure

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportParkedExpenditure
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectPartnerReportExpenditurePersistence {

    fun getPartnerReportExpenditureCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost>

    fun getPartnerReportExpenditureCosts(ids: Set<Long>, pageable: Pageable): Page<ProjectPartnerReportParkedExpenditure>

    fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
        doNotRenumber: Boolean = false,
    ): List<ProjectPartnerReportExpenditureCost>

    fun reIncludeParkedExpenditure(
        partnerId: Long,
        reportId: Long,
        expenditureId: Long,
    ): ProjectPartnerReportExpenditureCost

    fun markAsSampledAndLock(expenditureIds: Set<Long>)

    fun existsByExpenditureId(partnerId: Long, reportId: Long, expenditureId: Long): Boolean

    fun getExpenditureAttachment(partnerId: Long, expenditureId: Long): JemsFile?

    fun getAvailableLumpSums(partnerId: Long, reportId: Long): List<ProjectPartnerReportLumpSum>

    fun getAvailableUnitCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportUnitCost>

    fun getAvailableInvestments(partnerId: Long, reportId: Long): List<ProjectPartnerReportInvestment>

    fun getAvailableBudgetOptions(partnerId: Long, reportId: Long): ProjectPartnerBudgetOptions
}
