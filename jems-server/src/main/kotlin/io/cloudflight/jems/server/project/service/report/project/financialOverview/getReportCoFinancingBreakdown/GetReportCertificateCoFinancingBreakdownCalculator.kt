package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview.sum
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportCertificateCoFinancingBreakdownCalculator(
    private val reportPersistence: ProjectReportPersistence,
    private val reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence,
    private val reportCertificatePersistence: ProjectReportCertificatePersistence,
    private val reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence
) {

    @Transactional(readOnly = true)
    fun get(projectId: Long, reportId: Long): CertificateCoFinancingBreakdown {
        val report = reportPersistence.getReportById(projectId = projectId, reportId)

        val data = reportCertificateCoFinancingPersistence.getCoFinancing(projectId = projectId, reportId = reportId)
        val coFinancing = data.toLinesModel()

        if (!report.status.isClosed()) {
            val certificates = reportCertificatePersistence.listCertificatesOfProjectReport(reportId).map {
                reportExpenditureCoFinancingPersistence.getCoFinancing(it.partnerId, it.id).totalEligibleAfterControl
            }
            val fundIds = coFinancing.funds.map { it.fundId }

            val certificateCurrentValues = ReportCertificateCoFinancingColumn(
                funds = fundIds.associateBy({it}, {certificates.map { certificate ->  certificate.funds.getOrDefault(it, BigDecimal.ZERO) }.sum()}),
                partnerContribution = certificates.sumOf { it.partnerContribution },
                publicContribution = certificates.sumOf { it.publicContribution },
                automaticPublicContribution = certificates.sumOf { it.automaticPublicContribution },
                privateContribution = certificates.sumOf { it.privateContribution },
                sum = certificates.sumOf { it.sum }
            )

            coFinancing.fillInCurrent(current = certificateCurrentValues)
        }
        return coFinancing.fillInOverviewFields()
    }

}
