package io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadPaymentAdvAttachmentInteractor {

    fun upload(paymentId: Long, file: ProjectFile): JemsFileMetadata
}
