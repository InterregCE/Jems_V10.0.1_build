package io.cloudflight.jems.server.project.service.report.partner.control.file.uploadFileToCertificate

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadFileToCertificateInteractor {
    fun uploadToCertificate(
        partnerId: Long,
        reportId: Long,
        fileId: Long,
        file: ProjectFile,
    ): JemsFileMetadata
}
