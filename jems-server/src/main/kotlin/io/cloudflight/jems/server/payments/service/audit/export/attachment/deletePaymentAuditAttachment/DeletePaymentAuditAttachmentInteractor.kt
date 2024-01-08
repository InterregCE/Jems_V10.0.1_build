package io.cloudflight.jems.server.payments.service.audit.export.attachment.deletePaymentAuditAttachment

interface DeletePaymentAuditAttachmentInteractor {

    fun delete(fileId: Long)

}
