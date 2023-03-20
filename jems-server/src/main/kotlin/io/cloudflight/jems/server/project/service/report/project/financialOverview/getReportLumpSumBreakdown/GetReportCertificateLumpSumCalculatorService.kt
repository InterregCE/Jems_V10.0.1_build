package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class GetReportCertificateLumpSumCalculatorService(
    private val reportPersistence: ProjectReportPersistence,
    private val reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val reportExpenditureLumpSumPersistence: ProjectPartnerReportLumpSumPersistence
) {

    /**
     * This fun will either
     *  - just fetch reported certificate lump sum breakdown for submitted report
     *  - or calculate currently reported values from actual certificate lump sum
     */
    @Transactional(readOnly = true)
    fun getSubmittedOrCalculateCurrent(projectId: Long, reportId: Long): CertificateLumpSumBreakdown {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId).status

        val data = reportCertificateLumpSumPersistence.getLumpSums(projectId = projectId, reportId = reportId)

        if (report.isOpen()) {
            val certificates = reportCertificatePersistence.listCertificatesOfProjectReport(reportId)
            val currentLumpSums = reportExpenditureLumpSumPersistence.getLumpSumCumulativeAfterControl(certificates.map {it.id}.toSet())
            data.fillInCurrent(current = currentLumpSums)
        }
        val lumpSumLines = data.fillInOverviewFields()

        return CertificateLumpSumBreakdown(
            lumpSums = lumpSumLines,
            total = lumpSumLines.sumUp(),
        )
    }

    fun Collection<CertificateLumpSumBreakdownLine>.fillInCurrent(current: Map<Int, BigDecimal>) = apply {
        forEach {
            it.currentReport = current.get(it.orderNr) ?: BigDecimal.ZERO
        }
    }

    fun List<CertificateLumpSumBreakdownLine>.fillInOverviewFields() = apply {
        forEach { it.fillInOverviewFields() }
    }

    private fun CertificateLumpSumBreakdownLine.fillInOverviewFields() = apply {
        totalReportedSoFar = previouslyReported.plus(currentReport)
        totalReportedSoFarPercentage = if (totalEligibleBudget.compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else
            totalReportedSoFar.multiply(BigDecimal.valueOf(100)).divide(totalEligibleBudget, 2, RoundingMode.HALF_UP)
        remainingBudget = totalEligibleBudget.minus(totalReportedSoFar)
    }

    private fun emptyLine() = CertificateLumpSumBreakdownLine(
        reportLumpSumId = 0L,
        lumpSumId = 0L,
        period = null,
        orderNr = 0,
        name = emptySet(),
        totalEligibleBudget = BigDecimal.ZERO,
        previouslyReported = BigDecimal.ZERO,
        previouslyPaid = BigDecimal.ZERO,
        currentReport = BigDecimal.ZERO
    )

    fun List<CertificateLumpSumBreakdownLine>.sumUp() =
        fold(emptyLine()) { resultingTotalLine, lumpSum ->
            resultingTotalLine.totalEligibleBudget += lumpSum.totalEligibleBudget
            resultingTotalLine.previouslyReported += lumpSum.previouslyReported
            resultingTotalLine.previouslyPaid += lumpSum.previouslyPaid
            resultingTotalLine.currentReport += lumpSum.currentReport
            return@fold resultingTotalLine
        }.fillInOverviewFields()
}
