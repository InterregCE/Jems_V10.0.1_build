package io.cloudflight.jems.server.project.service.report.project.workPlan.uploadFileToProjectReportWorkPlan

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadFileToProjectReportWorkPlanInteractor {

    fun uploadToActivity(
        projectId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

    fun uploadToDeliverable(
        projectId: Long,
        reportId: Long,
        workPackageId: Long,
        activityId: Long,
        deliverableId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

    fun uploadToOutput(
        projectId: Long,
        reportId: Long,
        workPackageId: Long,
        outputId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

}
