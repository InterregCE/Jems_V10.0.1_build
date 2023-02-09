package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificateAttachment

interface DownloadReportControlCertificateAttachmentInteractor {

    fun downloadReportControlCertificateAttachment(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>
}
