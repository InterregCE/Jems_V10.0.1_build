package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing

import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportCoFinancingRepository
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerReportExpenditureCoFinancingPersistenceProvider(
    private val expenditureCoFinancingRepository: ReportProjectPartnerExpenditureCoFinancingRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
) : ProjectPartnerReportExpenditureCoFinancingPersistence {

    @Transactional(readOnly = true)
    override fun getCoFinancing(partnerId: Long, reportId: Long): ReportExpenditureCoFinancing =
        expenditureCoFinancingRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId)
            .toModel(
                coFinancing = partnerReportCoFinancingRepository
                    .findAllByIdReportIdOrderByIdFundSortNumber(reportId = reportId),
            )

    @Transactional(readOnly = true)
    override fun getCoFinancingCumulative(reportIds: Set<Long>): ExpenditureCoFinancingCurrent {
        val cumulativeByFund = partnerReportCoFinancingRepository.findCumulativeForReportIds(reportIds)
            .associateBy { it.reportFundId }
        return ExpenditureCoFinancingCurrent(
            current = with(expenditureCoFinancingRepository.findCumulativeForReportIds(reportIds)) {
                ReportExpenditureCoFinancingColumn(
                    funds = cumulativeByFund.mapValues { it.value.currentSum },
                    partnerContribution = partnerContribution,
                    publicContribution = publicContribution,
                    automaticPublicContribution = automaticPublicContribution,
                    privateContribution = privateContribution,
                    sum = sum,
                )
            },
            currentParked = with(expenditureCoFinancingRepository.findCumulativeParkedForReportIds(reportIds)) {
                ReportExpenditureCoFinancingColumn(
                    funds = cumulativeByFund.mapValues { it.value.currentParkedSum },
                    partnerContribution = partnerContribution,
                    publicContribution = publicContribution,
                    automaticPublicContribution = automaticPublicContribution,
                    privateContribution = privateContribution,
                    sum = sum,
                )
            }
        )
    }

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: ExpenditureCoFinancingCurrentWithReIncluded,
    ) {
        partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId)
            .forEachIndexed { index, coFin ->
                coFin.current = currentlyReported.current.funds.getOrDefault(
                    coFin.programmeFund?.id,
                    BigDecimal.ZERO
                )
                coFin.currentReIncluded = currentlyReported.currentReIncluded.funds.getOrDefault(
                    coFin.programmeFund?.id,
                    BigDecimal.ZERO
                )
            }

        expenditureCoFinancingRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId).apply {
                partnerContributionCurrent = currentlyReported.current.partnerContribution
                publicContributionCurrent = currentlyReported.current.publicContribution
                automaticPublicContributionCurrent = currentlyReported.current.automaticPublicContribution
                privateContributionCurrent = currentlyReported.current.privateContribution

                partnerContributionCurrentReIncluded = currentlyReported.currentReIncluded.partnerContribution
                publicContributionCurrentReIncluded = currentlyReported.currentReIncluded.publicContribution
                automaticPublicContributionCurrentReIncluded = currentlyReported.currentReIncluded.automaticPublicContribution
                privateContributionCurrentReIncluded = currentlyReported.currentReIncluded.privateContribution

                sumCurrent = currentlyReported.current.sum
                sumCurrentReIncluded = currentlyReported.currentReIncluded.sum
            }
    }

    @Transactional
    override fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControl: ExpenditureCoFinancingCurrent,
    ) {
        partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId)
            .forEachIndexed { index, coFin ->
                coFin.totalEligibleAfterControl = afterControl.current.funds.getOrDefault(coFin.programmeFund?.id, BigDecimal.ZERO)
                coFin.currentParked = afterControl.currentParked.funds.getOrDefault(coFin.programmeFund?.id, BigDecimal.ZERO)
            }

        expenditureCoFinancingRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId).apply {
                partnerContributionTotalEligibleAfterControl = afterControl.current.partnerContribution
                publicContributionTotalEligibleAfterControl = afterControl.current.publicContribution
                automaticPublicContributionTotalEligibleAfterControl = afterControl.current.automaticPublicContribution
                privateContributionTotalEligibleAfterControl = afterControl.current.privateContribution
                sumTotalEligibleAfterControl = afterControl.current.sum

                partnerContributionCurrentParked = afterControl.currentParked.partnerContribution
                publicContributionCurrentParked = afterControl.currentParked.publicContribution
                automaticPublicContributionCurrentParked = afterControl.currentParked.automaticPublicContribution
                privateContributionCurrentParked = afterControl.currentParked.privateContribution
                sumCurrentParked = afterControl.currentParked.sum
            }
    }

    @Transactional(readOnly = true)
    override fun getReportCurrentSum(partnerId: Long, reportId: Long) =
        expenditureCoFinancingRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(partnerId = partnerId, reportId = reportId)
            .sumCurrent


    @Transactional(readOnly = true)
    override fun getCoFinancingTotalEligible(reportIds: Set<Long>): ReportCertificateCoFinancingColumn =
        expenditureCoFinancingRepository
            .findCumulativeTotalsForReportIds(reportIds)
            .toModel(
                fundsData = partnerReportCoFinancingRepository
                    .findCumulativeTotalsForReportIds(reportIds),
            )
}
