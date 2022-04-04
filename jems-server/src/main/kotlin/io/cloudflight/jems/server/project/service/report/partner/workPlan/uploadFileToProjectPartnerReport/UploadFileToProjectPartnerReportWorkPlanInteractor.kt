package io.cloudflight.jems.server.project.service.report.partner.workPlan.uploadFileToProjectPartnerReport

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

interface UploadFileToProjectPartnerReportWorkPlanInteractor {

    fun uploadToActivity(
        partnerId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        file: ProjectFile,
    ): ProjectReportFileMetadata

    fun uploadToDeliverable(
        partnerId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        deliverableId: Long,
        file: ProjectFile,
    ): ProjectReportFileMetadata

    fun uploadToOutput(
        partnerId: Long,
        reportId: Long,
        workPackageId: Long,
        outputId: Long,
        file: ProjectFile,
    ): ProjectReportFileMetadata

}
