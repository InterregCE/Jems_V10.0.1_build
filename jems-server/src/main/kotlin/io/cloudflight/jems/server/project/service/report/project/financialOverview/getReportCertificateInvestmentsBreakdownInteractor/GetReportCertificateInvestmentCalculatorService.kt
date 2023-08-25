package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdownInteractor

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class GetReportCertificateInvestmentCalculatorService(
    private val reportPersistence: ProjectReportPersistence,
    private val reportCertificateInvestmentPersistence: ProjectReportCertificateInvestmentPersistence,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence
) {

    /**
     * This fun will either
     *  - just fetch reported certificate investment breakdown for submitted report
     *  - or calculate currently reported values from actual certificate investment
     */
    @Transactional(readOnly = true)
    fun getSubmittedOrCalculateCurrent(projectId: Long, reportId: Long): CertificateInvestmentBreakdown {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId).status

        val data = reportCertificateInvestmentPersistence.getInvestments(projectId = projectId, reportId = reportId)

        if (report.isOpenForNumbersChanges()) {
            val certificates = reportCertificatePersistence.listCertificatesOfProjectReport(reportId)
            val currentInvestments = reportInvestmentPersistence.getInvestmentsCumulativeAfterControl(certificates.map {it.id}.toSet())
            data.fillInCurrent(current = currentInvestments)
        }
        val investmentLines = data.fillInOverviewFields()

        return CertificateInvestmentBreakdown(
            investments = investmentLines,
            total = investmentLines.sumUp(),
        )
    }

    fun Collection<CertificateInvestmentBreakdownLine>.fillInCurrent(current: Map<Long, BigDecimal>) = apply {
        forEach {
            it.currentReport = current.get(it.investmentId) ?: BigDecimal.ZERO
        }
    }

    fun List<CertificateInvestmentBreakdownLine>.fillInOverviewFields() = apply {
        forEach { it.fillInOverviewFields() }
    }

    private fun CertificateInvestmentBreakdownLine.fillInOverviewFields() = apply {
        totalReportedSoFar = previouslyReported.plus(currentReport)
        totalReportedSoFarPercentage = if (totalEligibleBudget.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else
            totalReportedSoFar.multiply(BigDecimal.valueOf(100)).divide(totalEligibleBudget, 2, RoundingMode.HALF_UP)
        remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
    }

    private fun emptyLine() = CertificateInvestmentBreakdownLine(
        reportInvestmentId = 0L,
        investmentId = 0L,
        investmentNumber = 0,
        workPackageNumber = 0,
        title = emptySet(),
        deactivated = true,
        totalEligibleBudget = BigDecimal.ZERO,
        previouslyReported = BigDecimal.ZERO,
        currentReport = BigDecimal.ZERO,
        previouslyVerified = BigDecimal.ZERO,
        currentVerified = BigDecimal.ZERO,
    )

    fun List<CertificateInvestmentBreakdownLine>.sumUp() =
        fold(emptyLine()) { resultingTotalLine, lumpSum ->
            resultingTotalLine.totalEligibleBudget += lumpSum.totalEligibleBudget
            resultingTotalLine.previouslyReported += lumpSum.previouslyReported
            resultingTotalLine.currentReport += lumpSum.currentReport
            resultingTotalLine.previouslyVerified += lumpSum.previouslyVerified
            resultingTotalLine.currentVerified += lumpSum.currentVerified
            return@fold resultingTotalLine
        }.fillInOverviewFields()
}
