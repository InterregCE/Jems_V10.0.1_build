package io.cloudflight.jems.server.project.service.report.partner.file.uploadFileToProjectPartnerReport

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

interface UploadFileToProjectPartnerReportInteractor {

    fun uploadToReport(
        partnerId: Long,
        reportId: Long,
        file: ProjectFile,
    ): ProjectReportFileMetadata

}
