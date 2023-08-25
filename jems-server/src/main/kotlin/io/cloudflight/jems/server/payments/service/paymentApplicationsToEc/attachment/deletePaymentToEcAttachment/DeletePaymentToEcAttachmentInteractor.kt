package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.deletePaymentToEcAttachment

interface DeletePaymentToEcAttachmentInteractor {

    fun delete(fileId: Long)
}
