package io.cloudflight.jems.server.project.service.report.partner.file.downloadProjectPartnerReportFile

interface DownloadProjectPartnerReportFileInteractor {

    fun download(partnerId: Long, fileId: Long): Pair<String, ByteArray>

}
