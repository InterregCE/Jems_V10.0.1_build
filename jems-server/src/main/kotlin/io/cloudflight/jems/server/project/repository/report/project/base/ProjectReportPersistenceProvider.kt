package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.server.project.repository.contracting.reporting.ProjectContractingReportingRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportSpendingProfileRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

@Repository
class ProjectReportPersistenceProvider(
    private val projectReportRepository: ProjectReportRepository,
    private val contractingDeadlineRepository: ProjectContractingReportingRepository,
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val projectReportSpendingProfileRepository: ProjectReportSpendingProfileRepository,
) : ProjectReportPersistence {

    @Transactional(readOnly = true)
    override fun listReports(projectId: Long, pageable: Pageable): Page<ProjectReportModel> =
        projectReportRepository.findAllByProjectId(projectId, pageable).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getReportById(projectId: Long, reportId: Long): ProjectReportModel =
        projectReportRepository.getByIdAndProjectId(reportId, projectId = projectId).toModel()

    @Transactional
    override fun updateReport(
        projectId: Long,
        reportId: Long,
        startDate: LocalDate?,
        endDate: LocalDate?,
        deadline: ProjectReportDeadline,
    ): ProjectReportModel {
        val report = projectReportRepository.getByIdAndProjectId(reportId, projectId = projectId)

        report.startDate = startDate
        report.endDate = endDate
        report.deadline = deadline.deadlineId?.let { contractingDeadlineRepository.findByProjectIdAndId(projectId, it) }
        report.type = deadline.type
        report.periodNumber = deadline.periodNumber
        report.reportingDate = deadline.reportingDate

        return report.toModel()
    }

    @Transactional(readOnly = true)
    override fun getCurrentSpendingProfile(reportId: Long): Map<Long, BigDecimal> =
        partnerReportRepository.findTotalAfterControlPerPartner(reportId).toMap()

    @Transactional
    override fun updateSpendingProfile(reportId: Long, currentValuesByPartnerId: Map<Long, BigDecimal>) {
        val spendingProfiles = projectReportSpendingProfileRepository.findAllByIdProjectReportIdOrderByPartnerNumber(reportId)
        spendingProfiles.forEach { sp ->
            sp.currentlyReported = currentValuesByPartnerId.getOrDefault(sp.id.partnerId, BigDecimal.ZERO)
        }
    }

    @Transactional
    override fun deleteReport(projectId: Long, reportId: Long) =
        projectReportRepository.deleteByProjectIdAndId(projectId = projectId, reportId)

    @Transactional(readOnly = true)
    override fun getCurrentLatestReportFor(projectId: Long): ProjectReportModel? =
        projectReportRepository.findFirstByProjectIdOrderByIdDesc(projectId)?.toModel()

    @Transactional(readOnly = true)
    override fun countForProject(projectId: Long): Int =
        projectReportRepository.countAllByProjectId(projectId)

    @Transactional
    override fun submitReport(
        projectId: Long,
        reportId: Long,
        submissionTime: ZonedDateTime
    ): ProjectReportSubmissionSummary =
        projectReportRepository.getByIdAndProjectId(id = reportId, projectId = projectId)
            .apply {
                status = ProjectReportStatus.Submitted
                firstSubmission = submissionTime
            }.toSubmissionSummary()

    @Transactional(readOnly = true)
    override fun getSubmittedProjectReportIds(projectId: Long): List<Pair<Long, ContractingDeadlineType>> =
        projectReportRepository.findAllByProjectIdAndStatusInOrderByNumberDesc(projectId, statuses = ProjectReportStatus.SUBMITTED_STATUSES)
            .map { Pair(it.id, it.deadline?.type ?: it.type!!) }

    @Transactional(readOnly = true)
    override fun getDeadlinesWithLinkedReportStatus(projectId: Long): Map<Long, ProjectReportStatus> =
        projectReportRepository.findAllByProjectIdAndDeadlineNotNull(projectId)
            .groupBy { it.deadline!!.id }
            .mapValues { it.value.maxOf { it.status } }

    @Transactional
    override fun decreaseNewerReportNumbersIfAllOpen(projectId: Long, number: Int) {
        val lastReports = projectReportRepository.findAllByProjectIdAndNumberGreaterThan(projectId, number)
        if (lastReports.all { it.status.isOpen() }) {
            lastReports.onEach { it.number -= 1 }
        }
    }

    @Transactional(readOnly = true)
    override fun exists(projectId: Long, reportId: Long): Boolean =
        projectReportRepository.existsByProjectIdAndId(projectId, reportId)

}
