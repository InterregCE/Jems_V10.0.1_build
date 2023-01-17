package io.cloudflight.jems.server.project.service.report.partner.control.file.generateCertificate

import io.cloudflight.jems.api.common.dto.LogoType
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.PartnerControlReportCertificatePlugin
import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.project.controlCertificateCreated
import io.cloudflight.jems.server.resources.service.get_logos.GetLogoFailed
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class GenerateReportControlCertificate(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val securityService: SecurityService,
    private val partnerPersistence: PartnerPersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val jemsProjectFileService: JemsProjectFileService,
    private val getLogosInteractor: GetLogosInteractor,
    private val auditPublisher: ApplicationEventPublisher,
) : GenerateReportControlCertificateInteractor {

    @CanViewPartnerControlReport
    @ExceptionWrapper(GenerateReportControlCertificateException::class)
    override fun generateCertificate(partnerId: Long, reportId: Long) {

        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId, report.version)

        generateControlCertificateFileForReport(reportId, partnerId, projectId).also { certificate ->
            saveReportControlCertificate(certificate)
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
        projectId: Long
    ): JemsFileCreate {
        return jemsPluginRegistry.get(
            PartnerControlReportCertificatePlugin::class,
            "standard-partner-control-report-certificate-generate-plugin"
        ).generateCertificate(
            projectId = projectId,
            partnerId = partnerId,
            reportId = reportId,
            currentUser = getCurrentUserSummary(securityService.currentUser),
            logo = getMediumSizedLogo(),
            creationDate = LocalDateTime.now()
        ).toJemsFile(projectId, partnerId, reportId)
    }

    private fun saveReportControlCertificate(certificateFile: JemsFileCreate) {
        jemsProjectFileService.persistProjectFile(certificateFile)
    }

    private fun getCurrentUserSummary(currentUser: CurrentUser?) = UserSummaryData(
        id = currentUser?.user?.id ?: -1,
        email = currentUser?.user?.email ?: "",
        name = currentUser?.user?.name ?: "",
        surname = currentUser?.user?.surname ?: ""
    )


    private fun addIdentifierToFileName(fileName: String): String {
        val identifier = UUID.randomUUID().toString().split('-').first()
        val certificateFilePrefix = "Control Certificate"
        return fileName.replace(certificateFilePrefix, "$certificateFilePrefix $identifier ")
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
        name = addIdentifierToFileName(this.fileName),
        type = JemsFileType.ControlCertificate,
        path = JemsFileType.ControlCertificate.generatePath(projectId, partnerId, reportId),
        size = this.content.size.toLong(),
        content = this.content.inputStream(),
        userId = securityService.getUserIdOrThrow()
    )
}
