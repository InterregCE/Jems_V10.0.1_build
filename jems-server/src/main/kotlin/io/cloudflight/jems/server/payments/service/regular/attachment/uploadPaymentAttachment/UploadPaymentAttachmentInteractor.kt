package io.cloudflight.jems.server.payments.service.regular.attachment.uploadPaymentAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadPaymentAttachmentInteractor {

    fun upload(paymentId: Long, file: ProjectFile): JemsFileMetadata

}
