package io.cloudflight.jems.server.project.service.report.partner.workPlan.uploadFileToProjectPartnerReport

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
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
        activityId: Long,
        file: ProjectFile,
    ): ProjectReportFileMetadata {
        if (!reportWorkPlanPersistence.existsByActivityId(partnerId, reportId = reportId, activityId = activityId))
            throw ActivityNotFoundException(activityId = activityId)

        with(ProjectPartnerReportFileType.Activity) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId, activityId)

            return reportFilePersistence.updatePartnerReportActivityAttachment(
                activityId = activityId,
                file = file.getFileMetadata(projectId, partnerId, location, type = this),
            )
        }
    }

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectPartnerReportWorkPlanException::class)
    override fun uploadToDeliverable(
        partnerId: Long,
        reportId: Long,
        activityId: Long,
        deliverableId: Long,
        file: ProjectFile,
    ): ProjectReportFileMetadata {
        if (!reportWorkPlanPersistence.existsByDeliverableId(
                partnerId,
                reportId = reportId,
                activityId = activityId,
                deliverableId = deliverableId,
            ))
            throw DeliverableNotFoundException(deliverableId = deliverableId)

        with(ProjectPartnerReportFileType.Deliverable) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId, activityId, deliverableId)

            return reportFilePersistence.updatePartnerReportDeliverableAttachment(
                deliverableId = deliverableId,
                file = file.getFileMetadata(projectId, partnerId, location, type = this),
            )
        }
    }

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectPartnerReportWorkPlanException::class)
    override fun uploadToOutput(
        partnerId: Long,
        reportId: Long,
        outputId: Long,
        file: ProjectFile,
    ): ProjectReportFileMetadata {
        if (!reportWorkPlanPersistence.existsByOutputId(partnerId, reportId = reportId, outputId = outputId))
            throw OutputNotFoundException(outputId = outputId)

        with(ProjectPartnerReportFileType.Output) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId, outputId)

            return reportFilePersistence.updatePartnerReportOutputAttachment(
                outputId = outputId,
                file = file.getFileMetadata(projectId, partnerId, location, type = this),
            )
        }
    }

    private fun ProjectFile.getFileMetadata(
        projectId: Long,
        partnerId: Long,
        location: String,
        type: ProjectPartnerReportFileType,
    ) = ProjectReportFileCreate(
        projectId = projectId,
        partnerId = partnerId,
        name = name,
        path = location,
        type = type,
        size = size,
        content = stream,
        userId = securityService.getUserIdOrThrow(),
    )

}
