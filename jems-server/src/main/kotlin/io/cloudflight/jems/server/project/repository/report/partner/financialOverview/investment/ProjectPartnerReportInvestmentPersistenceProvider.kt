package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.investment

import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportInvestmentRepository
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Repository
class ProjectPartnerReportInvestmentPersistenceProvider(
    private val reportInvestmentRepository: ProjectPartnerReportInvestmentRepository,
): ProjectPartnerReportInvestmentPersistence {

    @Transactional(readOnly = true)
    override fun getInvestments(partnerId: Long, reportId: Long): List<ExpenditureInvestmentBreakdownLine> {
        return reportInvestmentRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(partnerId, reportId)
            .map { it.toModel() }
    }

    @Transactional(readOnly = true)
    override fun getInvestmentsCumulative(reportIds: Set<Long>) =
        reportInvestmentRepository.findCumulativeForReportIds(reportIds)
            .associate { Pair(it.first, ExpenditureInvestmentCurrent(current = it.second, currentParked = it.third)) }


    @Transactional(readOnly = true)
    override fun getVerificationParkedInvestmentsCumulative(partnerId: Long, projectReportIds: Set<Long>): Map<Long, BigDecimal> =
        reportInvestmentRepository.findVerificationParkedCumulativeForProjectReportIds(partnerId, projectReportIds).toMap()

    @Transactional
    override fun updateCurrentlyReportedValues(
        partnerId: Long,
        reportId: Long,
        currentlyReported: Map<Long, ExpenditureInvestmentCurrentWithReIncluded>,
    ) {
        reportInvestmentRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(partnerId = partnerId, reportId = reportId)
            .forEach {
                if (currentlyReported.containsKey(it.id)) {
                    it.current = currentlyReported.get(it.id)!!.current
                    it.currentReIncluded = currentlyReported.get(it.id)!!.currentReIncluded
                }
            }
    }

    @Transactional
    override fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControl: Map<Long, ExpenditureInvestmentCurrent>,
    ) {
        reportInvestmentRepository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(partnerId = partnerId, reportId = reportId)
            .forEach {
                if (afterControl.containsKey(it.id)) {
                    it.totalEligibleAfterControl = afterControl.get(it.id)!!.current
                    it.currentParked = afterControl.get(it.id)!!.currentParked
                }
            }
    }

    @Transactional
    override fun updateAfterVerificationParkedValues(parkedValuesPerCertificate: Map<Long, Map<Long, BigDecimal>>) {
        reportInvestmentRepository.findAllByReportEntityIdIn(parkedValuesPerCertificate.keys)
            .groupBy { it.reportEntity.id }
            .forEach {
                val parkedValues = parkedValuesPerCertificate[it.key]!!
                it.value.forEach { investmentEntity ->
                    if (parkedValues.containsKey(investmentEntity.investmentId)) {
                        investmentEntity.currentParkedVerification = parkedValues[investmentEntity.investmentId]!!
                    }
                }
            }

    }

    @Transactional(readOnly = true)
    override fun getInvestmentsCumulativeAfterControl(reportIds: Set<Long>) =
        reportInvestmentRepository.findCumulativeForReportIdsAfterControl(reportIds)
            .associate { Pair(it.first, it.second) }

}
