package io.cloudflight.jems.server.payments.service.account.attachment.uploadPaymentAccountAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadPaymentAccountAttachmentInteractor {

    fun upload(paymentAccountId: Long, file: ProjectFile): JemsFileMetadata

}
