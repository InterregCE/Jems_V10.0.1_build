package io.cloudflight.jems.server.payments.service.advance.attachment.deletePaymentAdvanceAttachment

interface DeletePaymentAdvAttachmentInteractor {
    fun delete(fileId: Long)
}