package io.cloudflight.jems.server.payments.service.attachment.uploadPaymentAttachment

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata

interface UploadPaymentAttachmentInteractor {

    fun upload(paymentId: Long, file: ProjectFile): ProjectReportFileMetadata

}
