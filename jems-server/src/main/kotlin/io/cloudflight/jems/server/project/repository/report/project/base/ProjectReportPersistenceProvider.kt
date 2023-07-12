package io.cloudflight.jems.server.project.repository.report.project.base

import com.querydsl.core.Tuple
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.plugin.contract.models.report.project.identification.ProjectReportBaseData
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.QProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.QReportProjectCertificateCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCoFinancingEntity
import io.cloudflight.jems.server.project.repository.contracting.reporting.ProjectContractingReportingRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportSpendingProfileRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ContractingDeadlineType
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.streams.asSequence
import org.springframework.data.domain.Sort

@Repository
class ProjectReportPersistenceProvider(
    private val projectReportRepository: ProjectReportRepository,
    private val contractingDeadlineRepository: ProjectContractingReportingRepository,
    private val partnerReportRepository: ProjectPartnerReportRepository,
    private val projectReportSpendingProfileRepository: ProjectReportSpendingProfileRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : ProjectReportPersistence {

    @Transactional(readOnly = true)
    override fun listReports(projectId: Long, pageable: Pageable): Page<ProjectReportModel> =
        fetchReports(projectId, pageable).map { it.toModel() }

    private fun fetchReports(projectId: Long, pageable: Pageable): Page<Pair<ProjectReportEntity, ReportProjectCertificateCoFinancingEntity?>> {
        val specReport = QProjectReportEntity.projectReportEntity
        val specReportCoFinancing = QReportProjectCertificateCoFinancingEntity.reportProjectCertificateCoFinancingEntity

        val results = jpaQueryFactory
            .select(specReport, specReportCoFinancing)
            .from(specReport)
                .leftJoin(specReportCoFinancing)
                    .on(specReport.id.eq(specReportCoFinancing.reportEntity.id))
            .where(specReport.projectId.eq(projectId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(pageable.sort.toQueryDslOrderBy())
            .fetchResults()

        return PageImpl(
            results.results.map { it: Tuple -> Pair(
                it.get(0, ProjectReportEntity::class.java)!!,
                it.get(1, ReportProjectCertificateCoFinancingEntity::class.java),
            ) },
            pageable,
            results.total,
        )
    }

    @Transactional(readOnly = true)
    override fun getAllProjectReportsBaseDataByProjectId(projectId: Long): Sequence<ProjectReportBaseData> =
        projectReportRepository.findAllProjectReportsBaseDataByProjectId(projectId).asSequence()

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

    @Transactional
    override fun startVerificationOnReportById(
        projectId: Long,
        reportId: Long
        ) =
        projectReportRepository.getByIdAndProjectId(id = reportId, projectId = projectId)
            .apply {
                status = ProjectReportStatus.InVerification
            }.toSubmissionSummary()

    private fun Sort.toQueryDslOrderBy(): OrderSpecifier<*> {
        val orderBy = if (isSorted) this.get().findFirst().get() else Sort.Order.desc("id")

        val specReport = QProjectReportEntity.projectReportEntity
        val dfg = when (orderBy.property) {
            "periodNumber" -> specReport.periodNumber
            "id" -> specReport.id
            else -> specReport.id
        }

        return OrderSpecifier(if (orderBy.isAscending) Order.ASC else Order.DESC, dfg)
    }

}