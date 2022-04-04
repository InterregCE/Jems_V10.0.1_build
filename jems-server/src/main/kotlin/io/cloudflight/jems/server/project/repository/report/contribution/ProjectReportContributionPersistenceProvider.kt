package io.cloudflight.jems.server.project.repository.report.contribution

import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.contribution.ProjectPartnerReportContributionEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
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
    private val reportFileRepository: ProjectReportFileRepository,
    private val minioStorage: MinioStorage,
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
        val reportEntity = reportRepository.getById(reportId)
        reportContributionRepository.saveAll(
            toBeCreated.map { it.toEntity(reportEntity, attachment = null) }
        )
    }

    private fun Collection<ProjectPartnerReportContributionEntity>.deleteAttachments() = map {
        it.attachment?.let { file ->
            minioStorage.deleteFile(bucket = file.minioBucket, filePath = file.minioLocation)
            reportFileRepository.delete(file)
        }
        it
    }

}
