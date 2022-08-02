package io.cloudflight.jems.server.project.service.export.export_application_form

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import java.time.LocalDateTime

interface ExportApplicationFormInteractor {
    fun export(
        projectId: Long,
        exportLanguage: SystemLanguage,
        inputLanguage: SystemLanguage,
        localDateTime: LocalDateTime,
        version: String? = null
    ): ExportResult
}
