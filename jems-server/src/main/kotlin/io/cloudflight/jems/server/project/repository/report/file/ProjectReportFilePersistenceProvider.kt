package io.cloudflight.jems.server.project.repository.report.file

import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.report.contribution.ProjectPartnerReportContributionRepository
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportExpenditureRepository
import io.cloudflight.jems.server.project.repository.report.toModel
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.workPlan.ProjectPartnerReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.user.repository.user.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class ProjectReportFilePersistenceProvider(
    private val reportFileRepository: ProjectReportFileRepository,
    private val minioStorage: MinioStorage,
    private val workPlanActivityRepository: ProjectPartnerReportWorkPackageActivityRepository,
    private val workPlanActivityDeliverableRepository: ProjectPartnerReportWorkPackageActivityDeliverableRepository,
    private val workPlanOutputRepository: ProjectPartnerReportWorkPackageOutputRepository,
    private val contributionRepository: ProjectPartnerReportContributionRepository,
    private val expenditureRepository: ProjectPartnerReportExpenditureRepository,
    private val userRepository: UserRepository,
) : ProjectReportFilePersistence {

    companion object {
        const val BUCKET = "project-report"
    }

    @Transactional(readOnly = true)
    override fun existsFile(partnerId: Long, fileId: Long) =
        reportFileRepository.existsByPartnerIdAndId(partnerId = partnerId, fileId = fileId)

    @Transactional(readOnly = true)
    override fun existsFile(location: String, fileName: String) =
        reportFileRepository.existsByPathAndName(path = location, name = fileName)

    @Transactional(readOnly = true)
    override fun downloadFile(partnerId: Long, fileId: Long) =
        reportFileRepository.findByPartnerIdAndId(partnerId = partnerId, fileId = fileId)?.let { file ->
            minioStorage.getFile(file.minioBucket, filePath = file.minioLocation).let {
                Pair(file.name, it)
            }
        }

    @Transactional
    override fun deleteFile(partnerId: Long, fileId: Long) =
        reportFileRepository.findByPartnerIdAndId(partnerId = partnerId, fileId = fileId)
            .deleteIfPresent()

    @Transactional
    override fun updatePartnerReportActivityAttachment(
        activityId: Long,
        file: ProjectReportFileCreate,
    ): ProjectReportFileMetadata {
        val activity = workPlanActivityRepository.findById(activityId).get()
        activity.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { activity.attachment = it }
    }

    @Transactional
    override fun updatePartnerReportDeliverableAttachment(
        deliverableId: Long,
        file: ProjectReportFileCreate,
    ): ProjectReportFileMetadata {
        val deliverable = workPlanActivityDeliverableRepository.findById(deliverableId).get()
        deliverable.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { deliverable.attachment = it }
    }

    @Transactional
    override fun updatePartnerReportOutputAttachment(
        outputId: Long,
        file: ProjectReportFileCreate
    ): ProjectReportFileMetadata {
        val output = workPlanOutputRepository.findById(outputId).get()
        output.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { output.attachment = it }
    }

    @Transactional
    override fun updatePartnerReportContributionAttachment(
        contributionId: Long,
        file: ProjectReportFileCreate
    ): ProjectReportFileMetadata {
        val contribution = contributionRepository.findById(contributionId).get()
        contribution.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { contribution.attachment = it }
    }

    @Transactional
    override fun updatePartnerReportExpenditureAttachment(
        expenditureId: Long,
        file: ProjectReportFileCreate
    ): ProjectReportFileMetadata {
        val expenditure = expenditureRepository.findById(expenditureId).get()
        expenditure.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { expenditure.attachment = it }
    }

    @Transactional(readOnly = true)
    override fun listAttachments(
        pageable: Pageable,
        indexPrefix: String,
        filterSubtypes: Set<ProjectPartnerReportFileType>,
        filterUserIds: Set<Long>,
    ): Page<ProjectReportFile> =
        reportFileRepository.filterAttachment(
            pageable = pageable,
            indexPrefix = indexPrefix,
            filterSubtypes = filterSubtypes,
            filterUserIds = filterUserIds,
        ).toModel()

    @Transactional
    override fun addAttachmentToPartnerReport(file: ProjectReportFileCreate) =
        persistFileAndUpdateLink(file = file) { /* we do not need to update any link */ }

    private fun persistFileAndUpdateLink(file: ProjectReportFileCreate, additionalStep: (ReportProjectFileEntity) -> Unit): ProjectReportFileMetadata {
        return persistAttachmentAndMetadata(file = file, locationForMinio = file.getMinioFullPath())
            .also { additionalStep.invoke(it) }
            .toModel()
    }

    private fun persistAttachmentAndMetadata(file: ProjectReportFileCreate, locationForMinio: String): ReportProjectFileEntity {
        minioStorage.saveFile(
            bucket = BUCKET,
            filePath = locationForMinio,
            size = file.size,
            stream = file.content,
            overwriteIfExists = true,
        )
        return reportFileRepository.save(
            file.toEntity(
                bucketForMinio = BUCKET,
                locationForMinio = locationForMinio,
                userResolver = { userRepository.getById(it) },
                uploaded = ZonedDateTime.now(),
            )
        )
    }

    private fun ReportProjectFileEntity?.deleteIfPresent() {
        if (this != null) {
            minioStorage.deleteFile(bucket = minioBucket, filePath = minioLocation)
            reportFileRepository.delete(this)
        }
    }

}
