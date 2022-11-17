package io.cloudflight.jems.server.payments.service.regular.attachment.downloadPaymentAttachment

interface DownloadPaymentAttachmentInteractor {

    fun download(fileId: Long): Pair<String, ByteArray>

}
