package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
            val certificates = reportCertificatePersistence.listCertificatesOfProjectReport(reportId)

            val certificateCurrentValues =
                reportExpenditureCoFinancingPersistence.getCoFinancingTotalEligible(certificates.map { it.id}.toSet())

            coFinancing.fillInCurrent(current = certificateCurrentValues)
        }
        return coFinancing.fillInOverviewFields()
    }

}
