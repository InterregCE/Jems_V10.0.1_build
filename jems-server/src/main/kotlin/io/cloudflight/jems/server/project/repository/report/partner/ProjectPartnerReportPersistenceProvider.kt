package io.cloudflight.jems.server.project.repository.report.partner

import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.ZonedDateTime

@Repository
class ProjectPartnerReportPersistenceProvider(
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository,
) : ProjectPartnerReportPersistence {

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

    @Transactional
    override fun startControlOnReportById(partnerId: Long, reportId: Long) =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
            .apply {
                status = ReportStatus.InControl
            }.toSubmissionSummary()

    @Transactional
    override fun finalizeControlOnReportById(
        partnerId: Long,
        reportId: Long,
        controlEnd: ZonedDateTime,
    ) = partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
        .apply {
            this.status = ReportStatus.Certified
            this.controlEnd = controlEnd
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
    override fun getSubmittedPartnerReportIds(partnerId: Long): Set<Long> =
        partnerReportRepository.findAllIdsByPartnerIdAndStatusIn(partnerId, ReportStatus.SUBMITTED_STATUSES)

    @Transactional(readOnly = true)
    override fun getReportIdsBefore(partnerId: Long, beforeReportId: Long): Set<Long> =
        partnerReportRepository.getReportIdsForPartnerBefore(partnerId = partnerId, reportId = beforeReportId)

    @Transactional(readOnly = true)
    override fun exists(partnerId: Long, reportId: Long) =
        partnerReportRepository.existsByPartnerIdAndId(partnerId = partnerId, id = reportId)

    @Transactional(readOnly = true)
    override fun getCurrentLatestReportForPartner(partnerId: Long): ProjectPartnerReport? =
        partnerReportRepository.findFirstByPartnerIdOrderByIdDesc(partnerId = partnerId)?.toModel(emptyList())

    @Transactional(readOnly = true)
    override fun countForPartner(partnerId: Long): Int =
        partnerReportRepository.countAllByPartnerId(partnerId)


    @Transactional(readOnly = true)
    override fun isAnyReportCreated() =
        partnerReportRepository.count() > 0

    @Transactional
    override fun deletePartnerReportById(reportId: Long) {
        partnerReportRepository.deleteById(reportId)
    }
}
