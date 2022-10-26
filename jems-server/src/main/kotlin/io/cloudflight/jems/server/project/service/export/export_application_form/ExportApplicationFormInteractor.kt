package io.cloudflight.jems.server.project.service.export.export_application_form

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import java.time.LocalDateTime

interface ExportApplicationFormInteractor {

    companion object {
        private const val DEFAULT_APPLICATION_EXPORT_PLUGIN = "standard-application-form-export-plugin"
    }

    fun export(
        projectId: Long,
        exportLanguage: SystemLanguage,
        inputLanguage: SystemLanguage,
        localDateTime: LocalDateTime,
        version: String? = null,
        pluginKey: String? = DEFAULT_APPLICATION_EXPORT_PLUGIN
    ): ExportResult
}
