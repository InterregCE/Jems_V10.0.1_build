package io.cloudflight.jems.server.project.service.report.project.verification.certificate.generate

import io.cloudflight.jems.api.common.dto.LogoType
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.project.report.ProjectReportVerificationCertificatePlugin
import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.server.authentication.model.CurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationPrivileged
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.project.verificationCertificateCreated
import io.cloudflight.jems.server.resources.service.get_logos.GetLogoFailed
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class GenerateVerificationCertificate(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val securityService: SecurityService,
    private val projectReportPersistence: ProjectReportPersistence,
    private val projectReportFilePersistence: ProjectReportFilePersistence,
    private val getLogosInteractor: GetLogosInteractor,
    private val auditPublisher: ApplicationEventPublisher,
) : GenerateVerificationCertificateInteractor {

    @CanEditReportVerificationPrivileged
    @Transactional
    @ExceptionWrapper(GenerateVerificationCertificateException::class)
    override fun generateCertificate(projectId: Long, reportId: Long, pluginKey: String) {
        val report = projectReportPersistence.getReportById(projectId = projectId, reportId = reportId)

        val certificateFile = generateVerificationCertificateForProjectReport(projectId = projectId, reportId = reportId, pluginKey = pluginKey)

        projectReportFilePersistence.saveVerificationCertificateFile(file = certificateFile)
        auditPublisher.publishEvent(
            verificationCertificateCreated(
                context = this,
                report = report
            )
        )
    }

    private fun generateVerificationCertificateForProjectReport(
        projectId: Long,
        reportId: Long,
        pluginKey: String,
    ): JemsFileCreate {
        return jemsPluginRegistry.get(
            ProjectReportVerificationCertificatePlugin::class,
            pluginKey
        ).generateCertificate(
            projectId = projectId,
            reportId = reportId,
            currentUser = getCurrentUserSummary(securityService.currentUser),
            logo = getMediumSizedLogo(),
            creationDate = LocalDateTime.now()
        ).toJemsFile(projectId, reportId)
    }

    private fun getCurrentUserSummary(currentUser: CurrentUser?) = UserSummaryData(
        id = currentUser?.user?.id ?: -1,
        email = currentUser?.user?.email ?: "",
        name = currentUser?.user?.name ?: "",
        surname = currentUser?.user?.surname ?: ""
    )

    private fun ExportResult.toJemsFile(projectId: Long, reportId: Long) = JemsFileCreate(
        projectId = projectId,
        partnerId = null,
        name = addIdentifierToFileName(fileName, projectId, reportId),
        type = JemsFileType.VerificationCertificate,
        path = JemsFileType.VerificationCertificate.generatePath(projectId, reportId),
        size = this.content.size.toLong(),
        content = this.content.inputStream(),
        userId = securityService.getUserIdOrThrow()
    )

    private fun addIdentifierToFileName(fileName: String, projectId: Long, reportId: Long): String {
        val identifier = projectReportFilePersistence.countProjectReportVerificationCertificates(projectId = projectId, reportId = reportId) + 1
        val certificateFilePrefix = "Verification Certificate"
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

}
