package io.cloudflight.jems.server.project.service.report.project.certificate

import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectPartnerReportIdentificationSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectReportCertificatePersistence {

    fun deselectCertificate(projectReportId: Long, certificateId: Long)

    fun selectCertificate(projectReportId: Long, certificateId: Long)

    fun listCertificates(partnerIds: Set<Long>, pageable: Pageable): Page<PartnerReportCertificate>

    fun listCertificatesOfProjectReport(projectReportId: Long): List<ProjectPartnerReportSubmissionSummary>

    fun getIdentificationSummariesOfProjectReport(projectReportId: Long): List<ProjectPartnerReportIdentificationSummary>

}
