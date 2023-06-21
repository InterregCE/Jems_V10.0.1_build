package io.cloudflight.jems.server.project.service.report.partner.file.control.uploadFileToControlReport

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadFileToControlReportInteractor {

    fun uploadToControlReport(
        partnerId: Long,
        reportId: Long,
        file: ProjectFile,
    ): JemsFileMetadata

}
