package io.cloudflight.jems.server.payments.service.ecPayment.attachment.downloadPaymentToEcAttachment

interface DownloadPaymentToEcAttachmentInteractor {

    fun download(fileId: Long): Pair<String, ByteArray>

}
