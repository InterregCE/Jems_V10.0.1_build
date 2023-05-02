package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.downloadProjectPartnerProcurementGdprFile

interface DownloadProjectPartnerReportGdprFileInteractor {

    fun download(partnerId: Long, fileId: Long): Pair<String, ByteArray>
}
