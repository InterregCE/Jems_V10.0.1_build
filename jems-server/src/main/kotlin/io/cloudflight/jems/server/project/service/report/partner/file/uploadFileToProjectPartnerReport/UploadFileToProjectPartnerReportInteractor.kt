package io.cloudflight.jems.server.project.service.report.partner.file.uploadFileToProjectPartnerReport

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadFileToProjectPartnerReportInteractor {

    fun uploadToReport(
        partnerId: Long,
        reportId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

}
