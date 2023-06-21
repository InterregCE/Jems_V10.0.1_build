package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown

import io.cloudflight.jems.server.project.service.report.fillInOverviewFields
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportCertificateUnitCostCalculatorService(
    private val reportPersistence: ProjectReportPersistence,
    private val reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence
) {

    /**
     * This fun will either
     *  - just fetch reported certificate unit cost breakdown for submitted report
     *  - or calculate currently reported values from actual certificate unit cost
     */
    @Transactional(readOnly = true)
    fun getSubmittedOrCalculateCurrent(projectId: Long, reportId: Long): CertificateUnitCostBreakdown {
        val report = reportPersistence.getReportById(projectId = projectId, reportId = reportId).status

        val data = reportCertificateUnitCostPersistence.getUnitCosts(projectId = projectId, reportId = reportId)

        if (report.isOpen()) {
            val certificates = reportCertificatePersistence.listCertificatesOfProjectReport(reportId)
            val currentUnitCosts = reportUnitCostPersistence.getUnitCostCumulativeAfterControl(certificates.map {it.id}.toSet())
            data.fillInCurrent(current = currentUnitCosts)
        }
        val unitCostLines = data.fillInOverviewFields()

        return CertificateUnitCostBreakdown(
            unitCosts = unitCostLines,
            total = unitCostLines.sumUp(),
        )
    }

    fun Collection<CertificateUnitCostBreakdownLine>.fillInCurrent(current: Map<Long, BigDecimal>) = apply {
        forEach {
            it.currentReport = current.get(it.unitCostId) ?: BigDecimal.ZERO
        }
    }

    private fun emptyLine() = CertificateUnitCostBreakdownLine(
        reportUnitCostId = 0L,
        unitCostId = 0L,
        name = emptySet(),
        totalEligibleBudget = BigDecimal.ZERO,
        previouslyReported = BigDecimal.ZERO,
        currentReport = BigDecimal.ZERO
    )

    fun List<CertificateUnitCostBreakdownLine>.sumUp() =
        fold(emptyLine()) { resultingTotalLine, lumpSum ->
            resultingTotalLine.totalEligibleBudget += lumpSum.totalEligibleBudget
            resultingTotalLine.previouslyReported += lumpSum.previouslyReported
            resultingTotalLine.currentReport += lumpSum.currentReport
            return@fold resultingTotalLine
        }.fillInOverviewFields()
}
