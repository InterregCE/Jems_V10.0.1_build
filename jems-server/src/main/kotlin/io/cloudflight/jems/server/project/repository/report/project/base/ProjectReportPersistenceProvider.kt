package io.cloudflight.jems.server.project.repository.report.project.base

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportIdentificationTargetGroupTranslEntity
import io.cloudflight.jems.server.project.repository.contracting.reporting.ProjectContractingReportingRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportIdentificationTargetGroupRepository
import io.cloudflight.jems.server.project.service.model.ProjectRelevanceBenefit
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportDeadline
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZonedDateTime

@Repository
class ProjectReportPersistenceProvider(
    private val projectReportRepository: ProjectReportRepository,
    private val contractingDeadlineRepository: ProjectContractingReportingRepository,
    private val reportIdentificationTargetGroupRepository: ProjectReportIdentificationTargetGroupRepository,
    private val partnerRepository: ProjectPartnerRepository,
    private val partnerReportRepository: ProjectPartnerReportRepository,
) : ProjectReportPersistence {

    @Transactional(readOnly = true)
    override fun listReports(projectId: Long, pageable: Pageable): Page<ProjectReportModel> =
        projectReportRepository.findAllByProjectId(projectId, pageable).map { it.toModel() }

    @Transactional(readOnly = true)
    override fun getReportById(projectId: Long, reportId: Long): ProjectReportModel =
        projectReportRepository.getByIdAndProjectId(reportId, projectId = projectId).toModel()

    @Transactional
    override fun createReportAndFillItToEmptyCertificates(report: ProjectReportModel, targetGroups: List<ProjectRelevanceBenefit>): ProjectReportModel {
        val reportPersisted = projectReportRepository
            .save(report.toEntity(deadlineResolver = { contractingDeadlineRepository.findByProjectIdAndId(report.projectId, it) }))

        createTargetGroups(targetGroups, reportPersisted)
        fillProjectReportToAllEmptyCertificates(projectId = report.projectId, reportPersisted)

        return reportPersisted.toModel()
    }

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

    private fun createTargetGroups(targetGroups: List<ProjectRelevanceBenefit>, reportEntity: ProjectReportEntity) {
        reportIdentificationTargetGroupRepository.saveAll(
            targetGroups.mapIndexed { index, targetGroup ->
                ProjectReportIdentificationTargetGroupEntity(
                    projectReportEntity = reportEntity,
                    type = ProjectTargetGroup.valueOf(targetGroup.group.name),
                    sortNumber = index.plus(1),
                    translatedValues = mutableSetOf(),
                ).apply {
                    translatedValues.addAll(
                        targetGroup.specification.map {
                            ProjectReportIdentificationTargetGroupTranslEntity(
                                translationId = TranslationId(this, it.language),
                                description = null,
                            )
                        }
                    )
                }
            }
        )
    }

    private fun fillProjectReportToAllEmptyCertificates(projectId: Long, report: ProjectReportEntity) {
        val partnerIds = partnerRepository.findTop30ByProjectId(projectId).mapTo(HashSet()) { it.id }

        partnerReportRepository.findAllByPartnerIdInAndProjectReportNullAndStatus(partnerIds, ReportStatus.Certified).forEach {
            it.projectReport = report
        }
    }

}
