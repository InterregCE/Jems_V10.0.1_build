package io.cloudflight.jems.server.programme.service.downloadProgrammeDataExportFile

import io.cloudflight.jems.plugin.contract.export.ExportResult

interface DownloadProgrammeDataExportFileInteractor {
    fun download(pluginKey: String?): ExportResult
}
