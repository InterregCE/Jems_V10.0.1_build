package io.cloudflight.jems.server.payments.service.attachment.setDescriptionToPaymentAttachment

interface SetDescriptionToPaymentAttachmentInteractor {

    fun setDescription(fileId: Long, description: String)

}
