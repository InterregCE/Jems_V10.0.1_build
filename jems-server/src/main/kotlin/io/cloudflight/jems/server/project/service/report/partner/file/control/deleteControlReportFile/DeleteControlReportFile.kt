package io.cloudflight.jems.server.project.service.report.partner.file.control.deleteControlReportFile

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.notification.handler.FileChangeAction
import io.cloudflight.jems.server.notification.handler.ProjectFileChangeEvent
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportFile
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.file.control.ControlReportFileAuthorizationService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteControlReportFile(
    private val filePersistence: JemsFilePersistence,
    private val authorization: ControlReportFileAuthorizationService,
    private val projectPersistence: ProjectPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val securityService: SecurityService,
    private val auditPublisher: ApplicationEventPublisher
) : DeleteControlReportFileInteractor {

    @CanEditPartnerControlReportFile
    @Transactional
    @ExceptionWrapper(DeleteControlReportFileException::class)
    override fun delete(partnerId: Long, reportId: Long, fileId: Long) {
        authorization.validateChangeToFileAllowed(partnerId = partnerId, reportId = reportId, fileId, true)
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val file = filePersistence.getFile(fileId, projectId)

        filePersistence.deleteFile(partnerId = partnerId, fileId = fileId).also {
            auditPublisher.publishEvent(
                ProjectFileChangeEvent(
                    action = FileChangeAction.Delete,
                    projectSummary = projectPersistence.getProjectSummary(projectId),
                    file = file!!,
                    overrideAuthorEmail = securityService.currentUser?.user?.email
                )
            )
        }
    }

}
