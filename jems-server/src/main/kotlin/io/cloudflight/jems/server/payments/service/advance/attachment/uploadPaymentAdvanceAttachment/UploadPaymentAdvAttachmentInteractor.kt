package io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadPaymentAdvAttachmentInteractor {

    fun upload(paymentId: Long, file: ProjectFile): JemsFileMetadata
}