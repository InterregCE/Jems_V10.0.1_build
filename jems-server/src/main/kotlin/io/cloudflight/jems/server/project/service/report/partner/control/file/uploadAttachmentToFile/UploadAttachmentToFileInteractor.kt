package io.cloudflight.jems.server.project.service.report.partner.control.file.uploadAttachmentToFile

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadAttachmentToFileInteractor {
    fun uploadAttachment(
        partnerId: Long,
        reportId: Long,
        fileId: Long,
        file: ProjectFile,
    ): JemsFileMetadata
}
