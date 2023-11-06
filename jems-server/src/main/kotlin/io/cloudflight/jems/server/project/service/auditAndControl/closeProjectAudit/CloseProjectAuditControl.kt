package io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanCloseProjectAuditAndControl
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.projectAuditControlClosed
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CloseProjectAuditControl(
    private val auditControlPersistence: AuditControlPersistenceProvider,
    private val auditControlValidator: ProjectAuditAndControlValidator,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistenceProvider: ProjectPersistenceProvider
) : CloseProjectAuditControlInteractor {

    @CanCloseProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(CloseProjectAuditControlException::class)
    override fun closeAuditControl(projectId: Long, auditControlId: Long): AuditStatus {
        val auditControl = auditControlPersistence.getByIdAndProjectId(projectId = projectId, auditControlId = auditControlId)
        auditControlValidator.verifyAuditControlOngoing(auditControl)
        auditControlValidator.validateAllCorrectionsAreClosed(auditControlId)

        val projectSummary = projectPersistenceProvider.getProjectSummary(projectId)
        return auditControlPersistence.updateProjectAuditStatus(
            projectId = projectId, auditControlId = auditControlId, auditStatus = AuditStatus.Closed
        ).also {
            auditPublisher.publishEvent(
                projectAuditControlClosed(
                    context = this,
                    projectSummary = projectSummary,
                    auditControl = it
                )
            )
        }.status
    }
}
