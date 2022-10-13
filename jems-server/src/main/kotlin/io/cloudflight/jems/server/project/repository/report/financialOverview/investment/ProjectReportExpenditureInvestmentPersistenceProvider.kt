package io.cloudflight.jems.server.project.repository.report.financialOverview.investment

import io.cloudflight.jems.server.project.service.report.model.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureInvestmentPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportExpenditureInvestmentPersistenceProvider(
    private val expenditureInvestmentRepository: ReportProjectPartnerExpenditureInvestmentRepository
): ProjectReportExpenditureInvestmentPersistence {

    @Transactional(readOnly = true)
    override fun getInvestments(partnerId: Long, reportId: Long): List<ExpenditureInvestmentBreakdownLine> {
        return expenditureInvestmentRepository.findByReportEntityPartnerIdAndReportEntityIdOrderByInvestmentIdAscIdAsc(partnerId, reportId).map { it.toModel() }
    }

    @Transactional(readOnly = true)
    override fun getInvestmentsCumulative(reportIds: Set<Long>) =
        expenditureInvestmentRepository.findCumulativeForReportIds(reportIds).toMap()

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: Map<Long, BigDecimal>,
    ) {
        expenditureInvestmentRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByInvestmentIdAscIdAsc(partnerId = partnerId, reportId = reportId)
            .forEach {
                if (currentlyReported.containsKey(it.id)) {
                    it.current = currentlyReported.get(it.id)!!
                }
            }
    }
}
