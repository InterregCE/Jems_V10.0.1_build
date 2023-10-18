package io.cloudflight.jems.server.project.repository.report.project.file

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationCertificate
import io.cloudflight.jems.server.project.repository.report.project.resultPrinciple.ProjectReportProjectResultRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.report.project.workPlan.ProjectReportWorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectReportFilePersistenceProvider(
    private val workPlanActivityRepository: ProjectReportWorkPackageActivityRepository,
    private val workPlanActivityDeliverableRepository: ProjectReportWorkPackageActivityDeliverableRepository,
    private val workPlanOutputRepository: ProjectReportWorkPackageOutputRepository,
    private val projectResultRepository: ProjectReportProjectResultRepository,
    private val fileService: JemsProjectFileService,
    private val jemsFileMetadataRepository: JemsFileMetadataRepository,
) : ProjectReportFilePersistence {

    @Transactional
    override fun updateReportActivityAttachment(activityId: Long, file: JemsFileCreate): JemsFileMetadata {
        val activity = workPlanActivityRepository.getById(activityId)
        activity.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { activity.attachment = it }
            .toSimple()
    }

    @Transactional
    override fun updateReportDeliverableAttachment(deliverableId: Long, file: JemsFileCreate): JemsFileMetadata {
        val deliverable = workPlanActivityDeliverableRepository.getById(deliverableId)
        deliverable.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { deliverable.attachment = it }
            .toSimple()
    }

    @Transactional
    override fun updateReportOutputAttachment(outputId: Long, file: JemsFileCreate): JemsFileMetadata {
        val output = workPlanOutputRepository.getById(outputId)
        output.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { output.attachment = it }
            .toSimple()
    }

    @Transactional
    override fun updateProjectResultAttachment(reportId: Long, resultNumber: Int, file: JemsFileCreate): JemsFileMetadata {
        val projectResult = projectResultRepository.findByProjectReportIdAndResultNumber(
            reportId = reportId, resultNumber = resultNumber
        )

        projectResult.attachment.deleteIfPresent()

        return persistFileAndUpdateLink(file = file) { projectResult.attachment = it }
            .toSimple()
    }

    @Transactional
    override fun addAttachmentToProjectReport(file: JemsFileCreate): JemsFile =
        fileService.persistFile(file)

    @Transactional
    override fun saveVerificationCertificateFile(file: JemsFileCreate): JemsFile =
        fileService.persistFile(file)

    @Transactional
    override fun saveAuditControlFile(file: JemsFileCreate): JemsFile =
        fileService.persistFile(file)

    @Transactional(readOnly = true)
    override fun countProjectReportVerificationCertificates(projectId: Long, reportId: Long): Long {
        val pathPrefix = JemsFileType.ProjectReport.generatePath(projectId, reportId)
        return jemsFileMetadataRepository.countByProjectIdAndPathPrefixAndType(projectId = projectId, pathPrefix = pathPrefix, type = VerificationCertificate)
    }

    private fun persistFileAndUpdateLink(file: JemsFileCreate, additionalStep: (JemsFileMetadataEntity) -> Unit) =
        fileService.persistFileAndPerformAction(file = file, additionalStep = additionalStep)

    private fun JemsFileMetadataEntity?.deleteIfPresent() {
        if (this != null) {
            fileService.delete(this)
        }
    }

}
