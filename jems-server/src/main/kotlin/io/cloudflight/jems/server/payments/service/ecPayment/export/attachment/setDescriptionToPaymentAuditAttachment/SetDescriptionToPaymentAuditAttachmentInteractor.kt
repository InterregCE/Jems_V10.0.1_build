package io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.setDescriptionToPaymentAuditAttachment

interface SetDescriptionToPaymentAuditAttachmentInteractor {

    fun setDescription(fileId: Long, description: String)

}
