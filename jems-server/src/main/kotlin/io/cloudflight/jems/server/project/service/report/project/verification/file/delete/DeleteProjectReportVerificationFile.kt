package io.cloudflight.jems.server.project.service.report.project.verification.file.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationCommunication
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectReportVerificationFile(
    private val filePersistence: JemsFilePersistence,
    private val projectPersistence: ProjectPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val reportPersistence: ProjectReportPersistence
) : DeleteProjectReportVerificationFileInteractor {

    @CanEditReportVerificationCommunication
    @Transactional
    @ExceptionWrapper(DeleteProjectReportVerificationFileException::class)
    override fun delete(projectId: Long, reportId: Long, fileId: Long) {
        val projectReport = reportPersistence.getReportById(projectId = projectId, reportId = reportId)
        if (!projectReport.status.canBeVerified())
            throw VerificationReportNotOngoing()

        val filePath = VerificationDocument.generatePath(projectId, reportId)
        val file = filePersistence.getFile(projectId = projectId, fileId) ?: throw FileNotFound()

        if (!filePersistence.existsFile(exactPath = filePath, fileId = fileId))
            throw FileNotFound()

        filePersistence.deleteFile(type = VerificationDocument, fileId = fileId).also {
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
