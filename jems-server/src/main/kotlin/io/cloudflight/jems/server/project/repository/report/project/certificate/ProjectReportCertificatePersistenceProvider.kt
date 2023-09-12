package io.cloudflight.jems.server.project.repository.report.project.certificate

import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.partner.toIdentificationSummaries
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.repository.report.partner.toSubmissionSummary
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.certificate.PartnerReportCertificate
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectPartnerReportIdentificationSummary
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
    override fun deselectCertificatesOfProjectReport(projectReportId: Long) {
        partnerReportRepository.findAllByProjectReportId(projectReportId)
            .forEach { it.projectReport = null }
    }

    @Transactional
    override fun deselectAllCertificatesForDeadlines(deadlineIds: Set<Long>) {
        partnerReportRepository.findAllByProjectReportDeadlineIdIn(deadlineIds)
            .forEach { it.projectReport = null }
    }

    @Transactional
    override fun selectCertificate(projectReportId: Long, certificateId: Long) {
        val partnerReport = partnerReportRepository.getById(certificateId)
        if (partnerReport.projectReport == null) {
            partnerReport.projectReport = projectReportRepository.getById(projectReportId)
        }
    }

    @Transactional(readOnly = true)
    override fun listCertificates(partnerIds: Set<Long>, pageable: Pageable): Page<PartnerReportCertificate> =
        partnerReportRepository.findAllCertificates(partnerIds, pageable).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun listCertificatesOfProjectReport(projectReportId: Long): List<ProjectPartnerReportSubmissionSummary> =
        partnerReportRepository.findAllByProjectReportId(projectReportId).map { it.toSubmissionSummary() }

    @Transactional(readOnly = true)
    override fun getIdentificationSummariesOfProjectReport(projectReportId: Long): List<ProjectPartnerReportIdentificationSummary> =
        partnerReportRepository.findAllIdentificationSummariesByProjectReportId(projectReportId).toIdentificationSummaries()

}
