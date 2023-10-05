package io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectAuditAndControl
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.toCreateModel
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControlUpdate
import io.cloudflight.jems.server.project.service.projectAuditControlCreated
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProjectAuditControl(
    private val auditControlPersistence: AuditControlPersistenceProvider,
    private val projectAuditAndControlValidator: ProjectAuditAndControlValidator,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistenceProvider: ProjectPersistenceProvider
) : CreateProjectAuditControlInteractor {

    @CanEditProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(CrateProjectAuditControlException::class)
    override fun createAudit(projectId: Long, auditControl: ProjectAuditControlUpdate): ProjectAuditControl {
        val auditsForProject = auditControlPersistence.countAuditsForProject(projectId)

        projectAuditAndControlValidator.validateMaxNumberOfAudits(auditsForProject)
        projectAuditAndControlValidator.validateData(auditControl)

        val projectSummary = projectPersistenceProvider.getProjectSummary(projectId)
        return auditControlPersistence.saveAuditControl(auditControl.toCreateModel(projectSummary, auditsForProject.toInt()))
            .also {
                auditPublisher.publishEvent(
                    projectAuditControlCreated(
                        context = this,
                        projectSummary = projectSummary,
                        auditControl = it
                    )
                )
            }
    }
}
