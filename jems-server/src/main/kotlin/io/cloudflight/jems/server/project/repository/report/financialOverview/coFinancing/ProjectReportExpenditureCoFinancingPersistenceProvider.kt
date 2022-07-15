package io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing

import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportCoFinancingRepository
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCoFinancingPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectReportExpenditureCoFinancingPersistenceProvider(
    private val expenditureCoFinancingRepository: ReportProjectPartnerExpenditureCoFinancingRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
) : ProjectReportExpenditureCoFinancingPersistence {

    @Transactional(readOnly = true)
    override fun getCoFinancing(partnerId: Long, reportId: Long): ReportExpenditureCoFinancing =
        expenditureCoFinancingRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId)
            .toModel(
                coFinancing = partnerReportCoFinancingRepository
                    .findAllByIdReportIdOrderByIdFundSortNumber(reportId = reportId),
            )

    @Transactional(readOnly = true)
    override fun getCoFinancingCumulative(reportIds: Set<Long>) =
        with(expenditureCoFinancingRepository.findCumulativeForReportIds(reportIds)) {
            ReportExpenditureCoFinancingColumn(
                funds = partnerReportCoFinancingRepository.findCumulativeForReportIds(reportIds).associateBy({ it.reportFundId }, { it.sum }),
                partnerContribution = partnerContribution,
                publicContribution = publicContribution,
                automaticPublicContribution = automaticPublicContribution,
                privateContribution = privateContribution,
                sum = sum,
            )
        }

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: ReportExpenditureCoFinancingColumn,
    ) {
        partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId)
            .forEachIndexed { index, coFin ->
                coFin.current = currentlyReported.funds.getOrDefault(coFin.programmeFund?.id, BigDecimal.ZERO)
            }

        expenditureCoFinancingRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId).apply {
                partnerContributionCurrent = currentlyReported.partnerContribution
                publicContributionCurrent = currentlyReported.publicContribution
                automaticPublicContributionCurrent = currentlyReported.automaticPublicContribution
                privateContributionCurrent = currentlyReported.privateContribution
                sumCurrent = currentlyReported.sum
            }
    }
}
