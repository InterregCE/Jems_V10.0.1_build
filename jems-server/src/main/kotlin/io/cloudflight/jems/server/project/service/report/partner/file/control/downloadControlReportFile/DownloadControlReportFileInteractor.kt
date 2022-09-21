package io.cloudflight.jems.server.project.service.report.partner.file.control.downloadControlReportFile

interface DownloadControlReportFileInteractor {

    fun download(partnerId: Long, fileId: Long): Pair<String, ByteArray>

}
