package io.cloudflight.jems.server.payments.service.advance.attachment.downloadPaymentAdvanceAttachment

interface DownloadPaymentAdvAttachmentInteractor {

    fun download(fileId: Long): Pair<String, ByteArray>
}