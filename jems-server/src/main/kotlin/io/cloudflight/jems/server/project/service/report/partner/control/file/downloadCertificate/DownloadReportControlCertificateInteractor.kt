package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadCertificate

interface DownloadReportControlCertificateInteractor {

    fun downloadReportControlCertificate(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>
}
