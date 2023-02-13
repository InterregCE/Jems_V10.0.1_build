package io.cloudflight.jems.server.project.service.report.partner.control.file.downloadFile

interface DownloadReportControlFileInteractor {

    fun download(partnerId: Long, reportId: Long, fileId: Long): Pair<String, ByteArray>
}
