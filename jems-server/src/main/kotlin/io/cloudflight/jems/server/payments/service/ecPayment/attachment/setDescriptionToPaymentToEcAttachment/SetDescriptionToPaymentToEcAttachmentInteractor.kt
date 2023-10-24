package io.cloudflight.jems.server.payments.service.ecPayment.attachment.setDescriptionToPaymentToEcAttachment

interface SetDescriptionToPaymentToEcAttachmentInteractor {

    fun setDescription(fileId: Long, description: String)

}
