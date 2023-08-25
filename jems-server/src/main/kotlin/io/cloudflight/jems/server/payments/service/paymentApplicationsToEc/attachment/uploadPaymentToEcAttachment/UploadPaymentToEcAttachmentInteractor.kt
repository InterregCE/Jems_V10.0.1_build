package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.uploadPaymentToEcAttachment

import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile

interface UploadPaymentToEcAttachmentInteractor {

    fun upload(paymentToEcId: Long, file: ProjectFile): JemsFileMetadata

}
