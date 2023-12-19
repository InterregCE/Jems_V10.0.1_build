package io.cloudflight.jems.server.payments.service.audit.export.attachment.uploadPaymentAuditAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadPaymentAuditAttachmentInteractor {

    fun upload(file: ProjectFile): JemsFileMetadata

}
