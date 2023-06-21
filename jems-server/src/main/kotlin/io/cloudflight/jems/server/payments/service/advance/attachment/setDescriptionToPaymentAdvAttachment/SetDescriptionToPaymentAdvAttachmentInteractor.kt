package io.cloudflight.jems.server.payments.service.advance.attachment.setDescriptionToPaymentAdvAttachment

interface SetDescriptionToPaymentAdvAttachmentInteractor {
    fun setDescription(fileId: Long, description: String)
}