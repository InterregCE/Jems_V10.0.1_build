package io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.downloadPaymentAuditAttachment

interface DownloadPaymentAuditAttachmentInteractor {

    fun download(fileId: Long): Pair<String, ByteArray>

}
