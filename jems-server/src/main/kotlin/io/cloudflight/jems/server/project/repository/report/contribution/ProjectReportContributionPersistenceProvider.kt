package io.cloudflight.jems.server.project.repository.report.contribution

import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionExisting
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportContributionPersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportContributionRepository: ProjectPartnerReportContributionRepository,
) : ProjectReportContributionPersistence {

    @Transactional(readOnly = true)
    override fun getPartnerReportContribution(
        partnerId: Long,
        reportId: Long,
    ): List<ProjectPartnerReportEntityContribution> =
        reportContributionRepository
            .findAllByReportEntityIdAndReportEntityPartnerIdOrderById(reportId, partnerId = partnerId)
            .toModel()

    @Transactional(readOnly = true)
    override fun getAllContributionsForReportIds(reportIds: Set<Long>): List<ProjectPartnerReportEntityContribution> =
        reportContributionRepository
            .findAllByReportEntityIdInOrderByReportEntityIdAscIdAsc(reportIds = reportIds)
            .toModel()

    @Transactional
    override fun deleteByIds(ids: Set<Long>) =
        reportContributionRepository.deleteAllById(ids)

    @Transactional
    override fun updateExisting(toBeUpdated: Collection<UpdateProjectPartnerReportContributionExisting>) {
        val toUpdateById = toBeUpdated.associateBy({ it.id }, { it.currentlyReported })
        reportContributionRepository.findAllById(toUpdateById.keys).forEach {
            it.currentlyReported = toUpdateById[it.id] ?: it.currentlyReported
        }
    }

    @Transactional
    override fun addNew(
        reportId: Long,
        toBeCreated: List<CreateProjectPartnerReportContribution>,
    ) {
        val reportEntity = reportRepository.getById(reportId)
        reportContributionRepository.saveAll(
            toBeCreated.map { it.toEntity(reportEntity) }
        )
    }
}
