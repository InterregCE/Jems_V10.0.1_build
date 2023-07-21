package io.cloudflight.jems.server.project.service.report.partner.file.control.uploadFileToControlReport

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.file.ProjectPartnerReportFilePersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToControlReport(
    private val reportFilePersistence: ProjectPartnerReportFilePersistence,
    private val filePersistence: JemsFilePersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val projectPersistence: ProjectPersistence,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher
) : UploadFileToControlReportInteractor {

    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(UploadFileToControlReportException::class)
    override fun uploadToControlReport(partnerId: Long, reportId: Long, file: ProjectFile): JemsFileMetadata {
        val reportSummary = reportPersistence.getProjectPartnerReportSubmissionSummary(partnerId, reportId = reportId)

        if (reportSummary.status.controlNotStartedYet())
            throw ReportNotInControl()

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.ControlDocument) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId)

            if (filePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists(file.name)
            val fileMetadata = file.getFileMetadata(projectId, partnerId, location, type = this, securityService.getUserIdOrThrow())

            return reportFilePersistence.addAttachmentToPartnerReport(
                file = fileMetadata,
            ).also {
                auditPublisher.publishEvent(
                    ProjectFileChangeEvent(
                        action = FileChangeAction.Upload,
                        projectSummary = projectPersistence.getProjectSummary(projectId),
                        file = it,
                    )
                )
            }.toSimple()
        }
    }
}
