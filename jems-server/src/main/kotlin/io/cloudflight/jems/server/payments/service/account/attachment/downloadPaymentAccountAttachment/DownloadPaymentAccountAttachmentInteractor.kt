package io.cloudflight.jems.server.payments.service.account.attachment.downloadPaymentAccountAttachment

interface DownloadPaymentAccountAttachmentInteractor {

    fun download(fileId: Long): Pair<String, ByteArray>

}
