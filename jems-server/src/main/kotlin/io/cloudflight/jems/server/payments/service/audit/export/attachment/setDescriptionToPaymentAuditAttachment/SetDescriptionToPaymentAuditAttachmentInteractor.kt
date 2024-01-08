package io.cloudflight.jems.server.payments.service.audit.export.attachment.setDescriptionToPaymentAuditAttachment

interface SetDescriptionToPaymentAuditAttachmentInteractor {

    fun setDescription(fileId: Long, description: String)

}
