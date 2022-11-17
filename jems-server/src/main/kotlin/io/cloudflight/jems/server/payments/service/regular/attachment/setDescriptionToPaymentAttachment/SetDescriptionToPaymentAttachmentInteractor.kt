package io.cloudflight.jems.server.payments.service.regular.attachment.setDescriptionToPaymentAttachment

interface SetDescriptionToPaymentAttachmentInteractor {

    fun setDescription(fileId: Long, description: String)

}
