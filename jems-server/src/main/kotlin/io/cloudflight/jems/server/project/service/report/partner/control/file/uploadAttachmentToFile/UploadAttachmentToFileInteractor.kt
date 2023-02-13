package io.cloudflight.jems.server.project.service.report.partner.control.file.uploadAttachmentToFile

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadAttachmentToFileInteractor {
    fun uploadAttachment(
        partnerId: Long,
        reportId: Long,
        fileId: Long,
        file: ProjectFile,
    ): JemsFileMetadata
}
