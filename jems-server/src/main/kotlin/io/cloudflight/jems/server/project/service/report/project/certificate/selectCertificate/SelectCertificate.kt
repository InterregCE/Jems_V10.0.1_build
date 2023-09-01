package io.cloudflight.jems.server.project.service.report.project.certificate.selectCertificate

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SelectCertificate(
    private val projectReportPersistence: ProjectReportPersistence,
    private val projectReportCertificatePersistence: ProjectReportCertificatePersistence,
    private val partnerReportPersistence: ProjectPartnerReportPersistence,
) : SelectCertificateInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(SelectCertificateException::class)
    override fun selectCertificate(projectId: Long, reportId: Long, certificateId: Long) {
        val projectReport = projectReportPersistence.getReportById(projectId, reportId = reportId)
        validateIsFinance(projectReport)

        val certificate = partnerReportPersistence.getPartnerReportByProjectIdAndId(projectId, certificateId)
            ?: throw CertificateNotFound()
        validateIsFinalized(certificate)

        projectReportCertificatePersistence.selectCertificate(projectReportId = projectReport.id, certificate.reportId)
    }

    private fun validateIsFinalized(certificate: ProjectPartnerReportStatusAndVersion) {
        if (!certificate.status.isFinalized())
            throw CertificateIsNotFinalized()
    }

    private fun validateIsFinance(report: ProjectReportModel) {
        if (!report.type!!.hasFinance())
            throw ProjectReportDoesNotIncludeFinance()
    }

}
