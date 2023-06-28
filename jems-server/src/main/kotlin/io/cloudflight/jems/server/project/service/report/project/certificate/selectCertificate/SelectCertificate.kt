package io.cloudflight.jems.server.project.service.report.project.certificate.selectCertificate

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
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
    private val projectPartnerReportPersistence: ProjectPartnerReportPersistence
) : SelectCertificateInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(SelectCertificateException::class)
    override fun selectCertificate(projectId: Long, reportId: Long, certificateId: Long) {
        val reportValidated = projectReportPersistence.getReportById(projectId, reportId = reportId)
        if (isCertificateInCorrectStatus(certificateId) && isProjectReportTheCorrectType(reportValidated)) {
            projectReportCertificatePersistence.selectCertificate(projectReportId = reportValidated.id, certificateId)
        }
    }

    private fun isCertificateInCorrectStatus(certificateId: Long) =
        projectPartnerReportPersistence.getReportStatusById(certificateId) == ReportStatus.Certified

    private fun isProjectReportTheCorrectType(report: ProjectReportModel) =
        report.type != ContractingDeadlineType.Content
}
