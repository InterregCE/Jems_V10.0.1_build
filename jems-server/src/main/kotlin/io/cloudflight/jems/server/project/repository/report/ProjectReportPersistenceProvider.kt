package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class ProjectReportPersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
) : ProjectReportPersistence {

    @Transactional
    override fun submitReportById(
        partnerId: Long,
        reportId: Long,
        submissionTime: ZonedDateTime
    ): ProjectPartnerReportSubmissionSummary =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
            .apply {
                status = ReportStatus.Submitted
                firstSubmission = submissionTime
            }.toSubmissionSummary()

    @Transactional(readOnly = true)
    override fun getPartnerReportStatusAndVersion(
        partnerId: Long,
        reportId: Long
    ): ProjectPartnerReportStatusAndVersion =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).let {
            ProjectPartnerReportStatusAndVersion(it.status, it.applicationFormVersion)
        }

    @Transactional(readOnly = true)
    override fun getPartnerReportById(partnerId: Long, reportId: Long): ProjectPartnerReport =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).toModel(
            coFinancing = partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId)
        )

    @Transactional(readOnly = true)
    override fun listPartnerReports(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary> =
        partnerReportRepository.findAllByPartnerId(partnerId = partnerId, pageable = pageable)
            .map { it.toModelSummary() }

    @Transactional(readOnly = true)
    override fun listSubmittedPartnerReports(partnerId: Long): List<ProjectPartnerReportSummary> =
        partnerReportRepository.findAllByPartnerIdAndStatus(partnerId, ReportStatus.Submitted).map { it.toModelSummary() }

    @Transactional(readOnly = true)
    override fun getReportIdsBefore(partnerId: Long, beforeReportId: Long): Set<Long> =
        partnerReportRepository.getReportIdsForPartnerBefore(partnerId = partnerId, reportId = beforeReportId)

    @Transactional(readOnly = true)
    override fun exists(partnerId: Long, reportId: Long) =
        partnerReportRepository.existsByPartnerIdAndId(partnerId = partnerId, id = reportId)

    @Transactional(readOnly = true)
    override fun getCurrentLatestReportNumberForPartner(partnerId: Long): Int =
        partnerReportRepository.getMaxNumberForPartner(partnerId = partnerId)

    @Transactional(readOnly = true)
    override fun countForPartner(partnerId: Long): Int =
        partnerReportRepository.countAllByPartnerId(partnerId)

}
