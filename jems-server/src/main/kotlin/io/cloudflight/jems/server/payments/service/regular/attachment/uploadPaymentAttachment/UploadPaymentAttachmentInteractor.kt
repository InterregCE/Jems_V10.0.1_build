package io.cloudflight.jems.server.payments.service.regular.attachment.uploadPaymentAttachment

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

interface UploadPaymentAttachmentInteractor {

    fun upload(paymentId: Long, file: ProjectFile): JemsFileMetadata

}
