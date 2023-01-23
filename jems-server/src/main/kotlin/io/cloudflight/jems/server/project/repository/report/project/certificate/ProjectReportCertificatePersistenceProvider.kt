package io.cloudflight.jems.server.project.repository.report.project.certificate

import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportCertificatePersistenceProvider(
    private val projectReportRepository: ProjectReportRepository,
    private val partnerReportRepository: ProjectPartnerReportRepository,
) : ProjectReportCertificatePersistence {

    @Transactional
    override fun deselectCertificate(projectReportId: Long, certificateId: Long) {
        val partnerReport = partnerReportRepository.getById(certificateId)
        if (partnerReport.projectReport?.id == projectReportId) {
            partnerReport.projectReport = null
        }
    }

    @Transactional
    override fun selectCertificate(projectReportId: Long, certificateId: Long) {
        val partnerReport = partnerReportRepository.getById(certificateId)
        if (partnerReport.projectReport == null) {
            partnerReport.projectReport = projectReportRepository.getById(projectReportId)
        }
    }

}
