package io.cloudflight.jems.server.project.repository.report

import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
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
    private val legalStatusRepository: ProgrammeLegalStatusRepository,
    private val programmeFundRepository: ProgrammeFundRepository,
) : ProjectReportPersistence {

    @Transactional
    override fun createPartnerReport(report: ProjectPartnerReportCreate): ProjectPartnerReportSummary {
        val reportEntity = persistReport(report)
        persistCoFinancingToReport(report.identification.coFinancing, report = reportEntity)
        return reportEntity.toModelSummary()
    }

    private fun persistReport(report: ProjectPartnerReportCreate): ProjectPartnerReportEntity =
        partnerReportRepository.save(
            report.toEntity(
                legalStatus = report.identification.legalStatusId?.let { legalStatusRepository.getById(it) }
            )
        )

    private fun persistCoFinancingToReport(
        coFinancing: List<ProjectPartnerCoFinancing>,
        report: ProjectPartnerReportEntity,
    ) {
        partnerReportCoFinancingRepository.saveAll(
            coFinancing.toEntity(
                reportEntity = report,
                programmeFundResolver = { programmeFundRepository.getById(it) },
            )
        )
    }

    @Transactional
    override fun submitReportById(partnerId: Long, reportId: Long, submissionTime: ZonedDateTime): ProjectPartnerReportSummary =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId)
            .apply {
                status = ReportStatus.Submitted
                firstSubmission = submissionTime
            }.toModelSummary()

    @Transactional(readOnly = true)
    override fun getPartnerReportById(partnerId: Long, reportId: Long): ProjectPartnerReport =
        partnerReportRepository.findByIdAndPartnerId(id = reportId, partnerId = partnerId).toModel(
            coFinancing = partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId)
        )

    @Transactional(readOnly = true)
    override fun listPartnerReports(partnerId: Long, pageable: Pageable): Page<ProjectPartnerReportSummary> =
        partnerReportRepository.findAllByPartnerId(partnerId = partnerId, pageable = pageable).map { it.toModelSummary() }

    @Transactional(readOnly = true)
    override fun getCurrentLatestReportNumberForPartner(partnerId: Long): Int =
        partnerReportRepository.getMaxNumberForPartner(partnerId = partnerId)

}
