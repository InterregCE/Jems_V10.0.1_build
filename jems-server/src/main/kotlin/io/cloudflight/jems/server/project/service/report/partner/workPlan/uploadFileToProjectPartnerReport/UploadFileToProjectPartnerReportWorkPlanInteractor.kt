package io.cloudflight.jems.server.project.service.report.partner.workPlan.uploadFileToProjectPartnerReport

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadFileToProjectPartnerReportWorkPlanInteractor {

    fun uploadToActivity(
        partnerId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

    fun uploadToDeliverable(
        partnerId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        deliverableId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

    fun uploadToOutput(
        partnerId: Long,
        reportId: Long,
        workPackageId: Long,
        outputId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

}
