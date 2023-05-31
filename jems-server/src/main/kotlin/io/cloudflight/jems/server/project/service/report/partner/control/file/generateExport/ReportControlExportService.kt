package io.cloudflight.jems.server.project.service.report.partner.control.file.generateExport

import io.cloudflight.jems.api.common.dto.LogoType
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerControlReportExportPlugin
import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import io.cloudflight.jems.server.project.service.report.project.controlReportCreated
import io.cloudflight.jems.server.resources.service.get_logos.GetLogoFailed
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import java.time.LocalDateTime
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReportControlExportService(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val securityService: SecurityService,
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence,
    private val getLogosInteractor: GetLogosInteractor,
    private val auditPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun generate(report: ProjectPartnerReport, partnerId: Long, projectId: Long, pluginKey: String){
        jemsPluginRegistry.get(PartnerControlReportExportPlugin::class, pluginKey).export(
            projectId = projectId,
            partnerId = partnerId,
            reportId = report.id,
            currentUser = getCurrentUserSummary(securityService.currentUser),
            logo = getMediumSizedLogo(),
            creationDate = LocalDateTime.now()
        ).toJemsFile(projectId, partnerId, report.id)
            .also { exportFile ->
                projectPartnerReportControlFilePersistence.saveReportControlFile(report.id, exportFile)
                auditPublisher.publishEvent(controlReportCreated(context = this, projectId = projectId, report = report))
            }
    }

    private fun getCurrentUserSummary(currentUser: CurrentUser?) = UserSummaryData(
        id = currentUser?.user?.id ?: -1,
        email = currentUser?.user?.email ?: "",
        name = currentUser?.user?.name ?: "",
        surname = currentUser?.user?.surname ?: ""
    )

    private fun addIdentifierToFileName(fileName: String, reportId: Long): String {
        val identifier = projectPartnerReportControlFilePersistence.countReportControlFilesByFileType(reportId = reportId, JemsFileType.ControlReport) + 1
        val exportFilePrefix = "Control Report"
        return fileName.replace(exportFilePrefix, "$exportFilePrefix $identifier")
    }

    private fun getMediumSizedLogo(): String? {
        return try {
            val logoDTOs = getLogosInteractor.getLogos()
                .filter { s -> s.logoType == LogoType.INTERREG_PROGRAMME_LOGO_MEDIUM }
            if (logoDTOs.isNotEmpty()) logoDTOs[0].value else null
        } catch (e: GetLogoFailed) {
            null
        }
    }

    private fun ExportResult.toJemsFile(projectId: Long, partnerId: Long, reportId: Long) = JemsFileCreate(
        projectId = projectId,
        partnerId = partnerId,
        name = addIdentifierToFileName(this.fileName, reportId),
        type = JemsFileType.ControlReport,
        path = JemsFileType.ControlReport.generatePath(projectId, partnerId, reportId),
        size = this.content.size.toLong(),
        content = this.content.inputStream(),
        userId = securityService.getUserIdOrThrow()
    )
}
