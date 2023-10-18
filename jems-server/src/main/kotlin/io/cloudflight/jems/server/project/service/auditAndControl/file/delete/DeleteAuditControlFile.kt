package io.cloudflight.jems.server.project.service.auditAndControl.file.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.authorization.CanEditProjectAuditAndControl
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteAuditControlFile(
    private val filePersistence: JemsFilePersistence,
    private val projectPersistence: ProjectPersistence,
    private val auditControlPersistence: AuditControlPersistence,
    private val auditAndControlValidator: ProjectAuditAndControlValidator,
    private val auditPublisher: ApplicationEventPublisher
) : DeleteAuditControlFileInteractor {

    @CanEditProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(DeleteAuditControlFileException::class)
    override fun delete(projectId: Long, auditControlId: Long, fileId: Long) {
        val filePath = JemsFileType.AuditControl.generatePath(projectId, auditControlId)
        val file = filePersistence.getFile(projectId = projectId, fileId) ?: throw FileNotFound()

        if (!filePersistence.existsFile(exactPath = filePath, fileId = fileId))
            throw FileNotFound()

        val auditControl = auditControlPersistence.getByIdAndProjectId(auditControlId = auditControlId, projectId = projectId)
        auditAndControlValidator.verifyAuditControlOngoing(auditControl)

        filePersistence.deleteFile(type = JemsFileType.AuditControl, fileId = fileId).also {
            auditPublisher.publishEvent(
                ProjectFileChangeEvent(
                    action = FileChangeAction.Delete,
                    projectSummary = projectPersistence.getProjectSummary(projectId),
                    file = file
                )
            )
        }
    }
}
