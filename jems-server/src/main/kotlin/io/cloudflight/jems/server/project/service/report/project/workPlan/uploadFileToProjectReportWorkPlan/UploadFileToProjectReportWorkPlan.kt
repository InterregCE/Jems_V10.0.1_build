package io.cloudflight.jems.server.project.service.report.project.workPlan.uploadFileToProjectReportWorkPlan

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToProjectReportWorkPlan(
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val reportWorkPlanPersistence: ProjectReportWorkPlanPersistence,
    private val securityService: SecurityService,
) : UploadFileToProjectReportWorkPlanInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectReportWorkPlanException::class)
    override fun uploadToActivity(
        projectId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        file: ProjectFile,
    ): JemsFileMetadata {
        if (!reportWorkPlanPersistence.existsByActivityId(projectId, reportId = reportId, workPackageId, activityId = activityId))
            throw ActivityNotFoundException(activityId = activityId)

        validateFile(file)

        with(JemsFileType.ActivityProjectReport) {
            val location = generatePath(projectId, reportId, workPackageId, activityId)

            return reportFilePersistence.updateReportActivityAttachment(
                activityId = activityId,
                file = file.getFileMetadata(projectId, null, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectReportWorkPlanException::class)
    override fun uploadToDeliverable(
        projectId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        deliverableId: Long,
        file: ProjectFile,
    ): JemsFileMetadata {
        if (!reportWorkPlanPersistence.existsByDeliverableId(
                projectId,
                reportId = reportId,
                workPackageId = workPackageId,
                activityId = activityId,
                deliverableId = deliverableId,
            ))
            throw DeliverableNotFoundException(deliverableId = deliverableId)

        validateFile(file)

        with(JemsFileType.DeliverableProjectReport) {
            val location = generatePath(projectId, reportId, workPackageId, activityId, deliverableId)

            return reportFilePersistence.updateReportDeliverableAttachment(
                deliverableId = deliverableId,
                file = file.getFileMetadata(projectId, null, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectReportWorkPlanException::class)
    override fun uploadToOutput(
        projectId: Long,
        reportId: Long,
        workPackageId: Long,
        outputId: Long,
        file: ProjectFile,
    ): JemsFileMetadata {
        if (!reportWorkPlanPersistence.existsByOutputId(projectId, reportId = reportId, workPackageId, outputId = outputId))
            throw OutputNotFoundException(outputId = outputId)

        validateFile(file)

        with(JemsFileType.OutputProjectReport) {
            val location = generatePath(projectId, reportId, workPackageId, outputId)

            return reportFilePersistence.updateReportOutputAttachment(
                outputId = outputId,
                file = file.getFileMetadata(projectId, null, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }

    private fun validateFile(file: ProjectFile) {
        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()
    }

}
