package io.cloudflight.jems.server.project.service.report.partner.file.control.uploadFileToControlReport

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

interface UploadFileToControlReportInteractor {

    fun uploadToControlReport(
        partnerId: Long,
        reportId: Long,
        file: ProjectFile,
    ): ProjectReportFileMetadata

}
