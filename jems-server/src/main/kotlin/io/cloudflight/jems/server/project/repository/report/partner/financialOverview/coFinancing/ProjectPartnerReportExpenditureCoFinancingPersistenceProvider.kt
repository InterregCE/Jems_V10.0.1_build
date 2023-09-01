package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing

import io.cloudflight.jems.server.programme.repository.fund.toModel
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportCoFinancingRepository
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingPrevious
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
    override fun getCoFinancingCumulative(submittedReportIds: Set<Long>, finalizedReportIds: Set<Long>): ExpenditureCoFinancingPrevious {
        val cumulativeByFund = partnerReportCoFinancingRepository.findCumulativeForReportIds(submittedReportIds)
            .filter { it.reportFundId != null }
            .associateBy { it.reportFundId }
        val cumulativeValidatedByFund = partnerReportCoFinancingRepository.findCumulativeTotalsForReportIds(finalizedReportIds)
            .filter { it.reportFundId != null }
            .associateBy { it.reportFundId }
        val fundSumWithoutPartnerContribution = cumulativeByFund.mapValues { it.value.currentSum }.values.sumOf{it}
        val fundSumWithoutPartnerContributionParked = cumulativeByFund.mapValues { it.value.currentParkedSum }.values.sumOf{it}
        val fundSumWithoutPartnerContributionValidated = cumulativeValidatedByFund.mapValues { it.value.sum }.values.sumOf{it}

        return ExpenditureCoFinancingPrevious(
            previous = with(expenditureCoFinancingRepository.findCumulativeForReportIds(submittedReportIds)) {
                ReportExpenditureCoFinancingColumn(
                    funds = cumulativeByFund.mapValues { it.value.currentSum }.plus(Pair(null, sum.minus(fundSumWithoutPartnerContribution))),
                    partnerContribution = sum.minus(fundSumWithoutPartnerContribution),
                    publicContribution = publicContribution,
                    automaticPublicContribution = automaticPublicContribution,
                    privateContribution = privateContribution,
                    sum = sum,
                )
            },
            previousParked = with(expenditureCoFinancingRepository.findCumulativeParkedForReportIds(submittedReportIds)) {
                ReportExpenditureCoFinancingColumn(
                    funds = cumulativeByFund.mapValues { it.value.currentParkedSum }.plus(Pair(null, sum.minus(fundSumWithoutPartnerContributionParked))),
                    partnerContribution = sum.minus(fundSumWithoutPartnerContributionParked),
                    publicContribution = publicContribution,
                    automaticPublicContribution = automaticPublicContribution,
                    privateContribution = privateContribution,
                    sum = sum,
                )
            },
            previousValidated = with(expenditureCoFinancingRepository.findCumulativeTotalsForReportIds(finalizedReportIds)) {
                ReportExpenditureCoFinancingColumn(
                    funds = cumulativeValidatedByFund.mapValues { it.value.sum }.plus(Pair(null, sum.minus(fundSumWithoutPartnerContributionValidated))),
                    partnerContribution = sum.minus(fundSumWithoutPartnerContributionValidated),
                    publicContribution = publicContribution,
                    automaticPublicContribution = automaticPublicContribution,
                    privateContribution = privateContribution,
                    sum = sum,
                )
            },
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
    override fun getCoFinancingTotalEligible(reportIds: Set<Long>): ReportCertificateCoFinancingColumn =
        expenditureCoFinancingRepository
            .findCumulativeTotalsForReportIds(reportIds)
            .toModel(
                fundsData = partnerReportCoFinancingRepository
                    .findCumulativeTotalsForReportIds(reportIds),
            )

    @Transactional(readOnly = true)
    override fun getTotalsForProjectReports(projectReportIds: Set<Long>): Map<Long, BigDecimal> =
        expenditureCoFinancingRepository
            .findCumulativeTotalsForProjectReportIds(projectReportIds = projectReportIds)
            .associate { it.first to it.second }

    @Transactional(readOnly = true)
    override fun getAvailableFunds(reportId: Long): List<ProgrammeFund> =
        partnerReportCoFinancingRepository.findAllByIdReportIdAndDisabledFalse(reportId)
            .mapNotNull { it.programmeFund?.toModel() }

}
