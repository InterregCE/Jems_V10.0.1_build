package io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate

interface GenerateReportControlCertificateInteractor {
    fun generateCertificate(partnerId: Long, reportId: Long, pluginKey: String)
}
