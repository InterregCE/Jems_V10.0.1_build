package io.cloudflight.jems.server.project.repository.report.financialOverview.investment

import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportInvestmentRepository
import io.cloudflight.jems.server.project.service.report.model.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportInvestmentPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportInvestmentPersistenceProvider(
    private val reportInvestmentRepository: ProjectPartnerReportInvestmentRepository,
): ProjectReportInvestmentPersistence {

    @Transactional(readOnly = true)
    override fun getInvestments(partnerId: Long, reportId: Long): List<ExpenditureInvestmentBreakdownLine> {
        return reportInvestmentRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(partnerId, reportId)
            .map { it.toModel() }
    }

    @Transactional(readOnly = true)
    override fun getInvestmentsCumulative(reportIds: Set<Long>) =
        reportInvestmentRepository.findCumulativeForReportIds(reportIds).toMap()

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: Map<Long, BigDecimal>,
    ) {
        reportInvestmentRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(partnerId = partnerId, reportId = reportId)
            .forEach {
                if (currentlyReported.containsKey(it.id)) {
                    it.current = currentlyReported.get(it.id)!!
                }
            }
    }
}
