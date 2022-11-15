package io.cloudflight.jems.server.project.service.report.partner.workPlan.uploadFileToProjectPartnerReport

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.workPlan.ProjectReportWorkPlanPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToProjectPartnerReportWorkPlan(
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val reportWorkPlanPersistence: ProjectReportWorkPlanPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val securityService: SecurityService,
) : UploadFileToProjectPartnerReportWorkPlanInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectPartnerReportWorkPlanException::class)
    override fun uploadToActivity(
        partnerId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        file: ProjectFile,
    ): JemsFileMetadata {
        if (!reportWorkPlanPersistence.existsByActivityId(partnerId, reportId = reportId, workPackageId, activityId = activityId))
            throw ActivityNotFoundException(activityId = activityId)

        validateFile(file)

        with(JemsFileType.Activity) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId, workPackageId, activityId)

            return reportFilePersistence.updatePartnerReportActivityAttachment(
                activityId = activityId,
                file = file.getFileMetadata(projectId, partnerId, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectPartnerReportWorkPlanException::class)
    override fun uploadToDeliverable(
        partnerId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        deliverableId: Long,
        file: ProjectFile,
    ): JemsFileMetadata {
        if (!reportWorkPlanPersistence.existsByDeliverableId(
                partnerId,
                reportId = reportId,
                workPackageId = workPackageId,
                activityId = activityId,
                deliverableId = deliverableId,
            ))
            throw DeliverableNotFoundException(deliverableId = deliverableId)

        validateFile(file)

        with(JemsFileType.Deliverable) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId, workPackageId, activityId, deliverableId)

            return reportFilePersistence.updatePartnerReportDeliverableAttachment(
                deliverableId = deliverableId,
                file = file.getFileMetadata(projectId, partnerId, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectPartnerReportWorkPlanException::class)
    override fun uploadToOutput(
        partnerId: Long,
        reportId: Long,
        workPackageId: Long,
        outputId: Long,
        file: ProjectFile,
    ): JemsFileMetadata {
        if (!reportWorkPlanPersistence.existsByOutputId(partnerId, reportId = reportId, workPackageId, outputId = outputId))
            throw OutputNotFoundException(outputId = outputId)

        validateFile(file)

        with(JemsFileType.Output) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId, workPackageId, outputId)

            return reportFilePersistence.updatePartnerReportOutputAttachment(
                outputId = outputId,
                file = file.getFileMetadata(projectId, partnerId, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }

    private fun validateFile(file: ProjectFile) {
        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()
    }

}
