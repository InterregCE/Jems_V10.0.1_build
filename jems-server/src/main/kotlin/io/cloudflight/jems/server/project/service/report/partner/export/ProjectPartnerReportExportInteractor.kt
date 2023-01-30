package io.cloudflight.jems.server.project.service.report.partner.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import java.time.LocalDateTime

interface ProjectPartnerReportExportInteractor {

    fun exportReport(
        partnerId: Long,
        reportId: Long,
        pluginKey: String,
        exportLanguage: SystemLanguage,
        inputLanguage: SystemLanguage,
        localDateTime: LocalDateTime
    ): ExportResult
}