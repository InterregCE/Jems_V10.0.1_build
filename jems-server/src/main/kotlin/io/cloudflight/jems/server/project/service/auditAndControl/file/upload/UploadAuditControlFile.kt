package io.cloudflight.jems.server.project.service.auditAndControl.file.upload

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.authorization.CanEditProjectAuditAndControl
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadAuditControlFile(
    private val filePersistence: JemsFilePersistence,
    private val projectReportFilePersistence: ProjectReportFilePersistence,
    private val securityService: SecurityService,
    private val projectPersistence: ProjectPersistence,
    private val auditControlPersistence: AuditControlPersistence,
    private val auditAndControlValidator: ProjectAuditAndControlValidator,
    private val auditPublisher: ApplicationEventPublisher
) : UploadAuditControlFileInteractor {

    @CanEditProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(UploadAuditControlFileException::class)
    override fun upload(projectId: Long, auditControlId: Long, file: ProjectFile): JemsFileMetadata {
        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.AuditControl) {
            val location = generatePath(projectId, auditControlId)

            if (filePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists(file.name)

            val auditControl = auditControlPersistence.getByIdAndProjectId(auditControlId = auditControlId, projectId = projectId)
            auditAndControlValidator.verifyAuditControlOngoing(auditControl)

            return projectReportFilePersistence.saveAuditControlFile(
                file = file.getFileMetadata(projectId, null, location, type = this, securityService.getUserIdOrThrow())
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
