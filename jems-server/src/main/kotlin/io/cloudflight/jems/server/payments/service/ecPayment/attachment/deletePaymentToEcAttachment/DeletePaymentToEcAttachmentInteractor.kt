package io.cloudflight.jems.server.payments.service.ecPayment.attachment.deletePaymentToEcAttachment

interface DeletePaymentToEcAttachmentInteractor {

    fun delete(fileId: Long)
}
