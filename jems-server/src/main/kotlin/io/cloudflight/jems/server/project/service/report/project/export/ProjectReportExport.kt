package io.cloudflight.jems.server.project.service.report.project.export

import io.cloudflight.jems.api.common.dto.LogoType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.project.report.ProjectReportExportPlugin
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.resources.service.get_logos.GetLogoFailed
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProjectReportExport(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val getLogosInteractor: GetLogosInteractor
) : ProjectReportExportInteractor {

    @CanRetrieveProjectReport
    @ExceptionWrapper(ProjectReportExportException::class)
    override fun exportReport(
        projectId: Long,
        reportId: Long,
        pluginKey: String,
        exportLanguage: SystemLanguage,
        inputLanguage: SystemLanguage,
        localDateTime: LocalDateTime
    ): ExportResult = jemsPluginRegistry.get(ProjectReportExportPlugin::class, pluginKey).export(
        projectId,
        reportId,
        SystemLanguageData.valueOf(exportLanguage.toString()),
        SystemLanguageData.valueOf(inputLanguage.toString()),
        logo = getMediumSizedLogo(),
        localDateTime = localDateTime
    )

    private fun getMediumSizedLogo(): String? {
        return try {
            val logoDTOs = getLogosInteractor.getLogos()
                .filter { s -> s.logoType == LogoType.INTERREG_PROGRAMME_LOGO_MEDIUM }
            if (logoDTOs.isNotEmpty()) logoDTOs[0].value else null
        } catch (e: GetLogoFailed) {
            null
        }
    }
}
