package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.downloadPaymentToEcAttachment

interface DownloadPaymentToEcAttachmentInteractor {

    fun download(fileId: Long): Pair<String, ByteArray>

}
