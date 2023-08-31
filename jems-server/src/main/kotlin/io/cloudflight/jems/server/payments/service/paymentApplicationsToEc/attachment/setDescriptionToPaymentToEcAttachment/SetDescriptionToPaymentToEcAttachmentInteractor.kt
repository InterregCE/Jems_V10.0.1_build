package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.setDescriptionToPaymentToEcAttachment

interface SetDescriptionToPaymentToEcAttachmentInteractor {

    fun setDescription(fileId: Long, description: String)

}
