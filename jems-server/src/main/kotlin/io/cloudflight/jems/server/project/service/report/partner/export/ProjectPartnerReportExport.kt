package io.cloudflight.jems.server.project.service.report.partner.export

import io.cloudflight.jems.api.common.dto.LogoType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerReportExportPlugin
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.resources.service.get_logos.GetLogoFailed
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProjectPartnerReportExport(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val getLogosInteractor: GetLogosInteractor
) : ProjectPartnerReportExportInteractor {

    @CanViewPartnerReport
    @ExceptionWrapper(ProjectPartnerReportExportException::class)
    override fun exportReport(
        partnerId: Long,
        reportId: Long,
        pluginKey: String,
        exportLanguage: SystemLanguage,
        inputLanguage: SystemLanguage,
        localDateTime: LocalDateTime
    ): ExportResult =
        jemsPluginRegistry.get(PartnerReportExportPlugin::class, pluginKey).export(
            partnerId,
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