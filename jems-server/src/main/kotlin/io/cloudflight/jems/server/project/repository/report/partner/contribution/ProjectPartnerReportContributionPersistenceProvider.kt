package io.cloudflight.jems.server.project.repository.report.partner.contribution

import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.project.entity.report.partner.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.update.UpdateProjectPartnerReportContributionExisting
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerReportContributionPersistenceProvider(
    private val reportRepository: ProjectPartnerReportRepository,
    private val reportContributionRepository: ProjectPartnerReportContributionRepository,
    private val fileRepository: JemsProjectFileService,
) : ProjectPartnerReportContributionPersistence {

    @Transactional(readOnly = true)
    override fun getPartnerReportContribution(
        partnerId: Long,
        reportId: Long,
    ): List<ProjectPartnerReportEntityContribution> =
        reportContributionRepository
            .findAllByReportEntityIdAndReportEntityPartnerIdOrderById(reportId, partnerId = partnerId)
            .toModel()

    @Transactional(readOnly = true)
    override fun existsByContributionId(partnerId: Long, reportId: Long, contributionId: Long) =
        reportContributionRepository
            .existsByReportEntityPartnerIdAndReportEntityIdAndId(partnerId = partnerId, reportId, contribId = contributionId)

    @Transactional(readOnly = true)
    override fun getAllContributionsForReportIds(reportIds: Set<Long>): List<ProjectPartnerReportEntityContribution> =
        reportContributionRepository
            .findAllByReportEntityIdInOrderByReportEntityIdAscIdAsc(reportIds = reportIds)
            .toModel()

    @Transactional
    override fun deleteByIds(ids: Set<Long>) =
        reportContributionRepository.deleteAll(
            reportContributionRepository.findAllById(ids).deleteAttachments()
        )

    @Transactional
    override fun updateExisting(toBeUpdated: Collection<UpdateProjectPartnerReportContributionExisting>) {
        val toUpdateById = toBeUpdated.associateBy { it.id }
        reportContributionRepository.findAllById(toUpdateById.keys).forEach {
            it.currentlyReported = toUpdateById[it.id]?.currentlyReported ?: it.currentlyReported
            it.sourceOfContribution = toUpdateById[it.id]?.sourceOfContribution ?: it.sourceOfContribution
            it.legalStatus = toUpdateById[it.id]?.legalStatus ?: it.legalStatus
        }
    }

    @Transactional
    override fun addNew(
        reportId: Long,
        toBeCreated: List<CreateProjectPartnerReportContribution>,
    ) {
        val reportEntity = reportRepository.getReferenceById(reportId)
        reportContributionRepository.saveAll(
            toBeCreated.map { it.toEntity(reportEntity, attachment = null) }
        )
    }

    private fun Collection<ProjectPartnerReportContributionEntity>.deleteAttachments() = map {
        it.attachment?.let { file -> fileRepository.delete(file) }
        it
    }

}
