package io.cloudflight.jems.server.payments.service.account.attachment.setDescriptionToPaymentAccountAttachment

interface SetDescriptionToPaymentAccountAttachmentInteractor {

    fun setDescription(fileId: Long, description: String)

}
