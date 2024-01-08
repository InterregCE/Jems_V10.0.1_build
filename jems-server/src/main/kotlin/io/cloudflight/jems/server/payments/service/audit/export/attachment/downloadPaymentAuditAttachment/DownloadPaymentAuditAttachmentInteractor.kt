package io.cloudflight.jems.server.payments.service.audit.export.attachment.downloadPaymentAuditAttachment

interface DownloadPaymentAuditAttachmentInteractor {

    fun download(fileId: Long): Pair<String, ByteArray>

}
