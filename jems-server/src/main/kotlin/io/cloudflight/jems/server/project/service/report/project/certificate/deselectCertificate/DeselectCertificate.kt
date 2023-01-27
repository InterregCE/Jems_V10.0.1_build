package io.cloudflight.jems.server.project.service.report.project.certificate.deselectCertificate

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeselectCertificate(
    private val projectReportPersistence: ProjectReportPersistence,
    private val projectReportCertificatePersistence: ProjectReportCertificatePersistence,
) : DeselectCertificateInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(DeselectCertificateException::class)
    override fun deselectCertificate(projectId: Long, reportId: Long, certificateId: Long) {
        val reportIdValidated = projectReportPersistence.getReportById(projectId, reportId = reportId).id
        projectReportCertificatePersistence.deselectCertificate(projectReportId = reportIdValidated, certificateId)
    }

}
