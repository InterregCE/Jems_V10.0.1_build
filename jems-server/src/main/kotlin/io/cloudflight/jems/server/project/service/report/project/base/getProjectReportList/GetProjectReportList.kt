package io.cloudflight.jems.server.project.service.report.project.base.getProjectReportList

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.toServiceSummaryModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectReportList(
    private val reportPersistence: ProjectReportPersistence,
    private val projectPersistence: ProjectPersistence,
    private val certificateCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
) : GetProjectReportListInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportListException::class)
    override fun findAll(projectId: Long, pageable: Pageable): Page<ProjectReportSummary> {
        val reports = reportPersistence.listReports(projectId, pageable)
            .map { it.toServiceSummaryModel(it.periodResolver()) }

        val amountsForDraftReports = getAmountsForDraftReports(reports.content)

        return reports
            .fillInAmountsForDraftReports(amountsForDraftReports)
            .removeZeroAmountsFromContentReports()
    }

    private fun ProjectReportModel.periodResolver(): (Int) -> ProjectPeriod? = { periodNumber ->
        projectPersistence.getProjectPeriods(projectId, linkedFormVersion)
            .firstOrNull { it.number == periodNumber }
    }

    private fun getAmountsForDraftReports(reports: List<ProjectReportSummary>): Map<Long, BigDecimal> {
        val draftReportIds = reports.filter { it.status.isOpenForNumbersChanges() }.mapTo(HashSet()) { it.id }
        return if (draftReportIds.isEmpty()) emptyMap() else
            certificateCoFinancingPersistence.getTotalsForProjectReports(projectReportIds = draftReportIds)
    }

    private fun Page<ProjectReportSummary>.fillInAmountsForDraftReports(byId: Map<Long, BigDecimal>) = this.onEach {
        if (byId.containsKey(it.id))
            it.amountRequested = byId[it.id]
    }

    private fun Page<ProjectReportSummary>.removeZeroAmountsFromContentReports() = this.onEach { report ->
        val amount = report.amountRequested
        val totalEligible = report.totalEligibleAfterVerification

        if (amount.isZero() && report.doesNotHaveFinance())
            report.amountRequested = null

        if (totalEligible.isZero() && (report.doesNotHaveFinance() || !report.status.isFinalized()))
            report.totalEligibleAfterVerification = null
    }

    private fun BigDecimal?.isZero() = this != null && this.compareTo(BigDecimal.ZERO) == 0

}
