package io.cloudflight.jems.server.payments.service.attachment.downloadPaymentAttachment

interface DownloadPaymentAttachmentInteractor {

    fun download(fileId: Long): Pair<String, ByteArray>

}
