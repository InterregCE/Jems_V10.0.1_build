package io.cloudflight.jems.server.project.service.auditAndControl.base.createAuditControl

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditAuditControlForProject
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.toCreateModel
import io.cloudflight.jems.server.project.service.auditAndControl.validator.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import io.cloudflight.jems.server.project.service.projectAuditControlCreated
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateAuditControl(
    private val auditControlPersistence: AuditControlPersistenceProvider,
    private val projectAuditAndControlValidator: ProjectAuditAndControlValidator,
    private val auditPublisher: ApplicationEventPublisher,
) : CreateAuditControlInteractor {

    companion object {
        private const val MAX_NUMBER_OF_AUDITS = 100
    }

    @CanEditAuditControlForProject
    @Transactional
    @ExceptionWrapper(CrateProjectAuditControlException::class)
    override fun createAudit(projectId: Long, auditControl: AuditControlUpdate): AuditControl {
        val auditsAmount = auditControlPersistence.countAuditsForProject(projectId)

        validateMaxNumberOfAudits(numberOfExistingAudits = auditsAmount)
        projectAuditAndControlValidator.validateData(auditControl)

        val toCreate = auditControl.toCreateModel(sortNumber = auditsAmount.plus(1))
        return auditControlPersistence.createControl(projectId, toCreate)
            .also {
                auditPublisher.publishEvent(
                    projectAuditControlCreated(context = this, auditControl = it)
                )
            }
    }

    fun validateMaxNumberOfAudits(numberOfExistingAudits: Int) {
        if (numberOfExistingAudits >= MAX_NUMBER_OF_AUDITS)
            throw MaxNumberOfAuditsReachedException()
    }

}
