package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFileAttachment

interface DownloadReportControlFileAttachmentInteractor {

    fun downloadReportControlCertificateAttachment(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>
}
