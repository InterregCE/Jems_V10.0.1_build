package io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport

interface GenerateReportControlExportInteractor {
    fun export(partnerId: Long, reportId: Long, pluginKey: String)
}
