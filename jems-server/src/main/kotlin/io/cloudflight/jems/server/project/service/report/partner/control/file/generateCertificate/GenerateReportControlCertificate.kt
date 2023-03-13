package io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate

import io.cloudflight.jems.api.common.dto.LogoType
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerControlReportCertificatePlugin
import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import io.cloudflight.jems.server.project.service.report.project.controlCertificateCreated
import io.cloudflight.jems.server.resources.service.get_logos.GetLogoFailed
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class GenerateReportControlCertificate(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val securityService: SecurityService,
    private val partnerPersistence: PartnerPersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence,
    private val getLogosInteractor: GetLogosInteractor,
    private val auditPublisher: ApplicationEventPublisher,
) : GenerateReportControlCertificateInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(GenerateReportControlCertificateException::class)
    override fun generateCertificate(partnerId: Long, reportId: Long, pluginKey: String) {

        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId, report.version)

        generateControlCertificateFileForReport(reportId, partnerId, projectId, pluginKey).also { certificateFile ->
            projectPartnerReportControlFilePersistence.saveReportControlFile(report.id, certificateFile)
            auditPublisher.publishEvent(
                controlCertificateCreated(
                    context = this,
                    projectId = projectId,
                    report = report
                )
            )
        }
    }

    private fun generateControlCertificateFileForReport(
        reportId: Long,
        partnerId: Long,
        projectId: Long,
        pluginKey: String
    ): JemsFileCreate {
        return jemsPluginRegistry.get(
            PartnerControlReportCertificatePlugin::class,
            pluginKey
        ).generateCertificate(
            projectId = projectId,
            partnerId = partnerId,
            reportId = reportId,
            currentUser = getCurrentUserSummary(securityService.currentUser),
            logo = getMediumSizedLogo(),
            creationDate = LocalDateTime.now()
        ).toJemsFile(projectId, partnerId, reportId)
    }

    private fun getCurrentUserSummary(currentUser: CurrentUser?) = UserSummaryData(
        id = currentUser?.user?.id ?: -1,
        email = currentUser?.user?.email ?: "",
        name = currentUser?.user?.name ?: "",
        surname = currentUser?.user?.surname ?: ""
    )

    private fun addIdentifierToFileName(fileName: String, reportId: Long): String {
        val identifier = projectPartnerReportControlFilePersistence.countReportControlFilesByFileType(reportId = reportId, JemsFileType.ControlCertificate) + 1
        val certificateFilePrefix = "Control Certificate"
        return fileName.replace(certificateFilePrefix, "$certificateFilePrefix $identifier")
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
        type = JemsFileType.ControlCertificate,
        path = JemsFileType.ControlCertificate.generatePath(projectId, partnerId, reportId),
        size = this.content.size.toLong(),
        content = this.content.inputStream(),
        userId = securityService.getUserIdOrThrow()
    )
}
