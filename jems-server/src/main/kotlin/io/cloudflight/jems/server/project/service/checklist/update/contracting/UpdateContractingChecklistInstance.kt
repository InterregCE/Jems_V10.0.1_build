package io.cloudflight.jems.server.project.service.checklist.update.contracting

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstanceValidator
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectContractingChecklistStatusChanged
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateContractingChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val checklistInstanceValidator: ChecklistInstanceValidator,
    private val userAuthorization: UserAuthorization,
) : UpdateContractingChecklistInstanceInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(UpdateContractingChecklistInstanceException::class)
    override fun update(projectId: Long, checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        val existing = persistence.getChecklistDetail(checklist.id, ProgrammeChecklistType.CONTRACTING, projectId)

        if (existing.status != checklist.status || (userAuthorization.getUser().email != existing.creatorEmail))
            throw UpdateContractingChecklistInstanceStatusNotAllowedException()

        checklistInstanceValidator.validateChecklistComponents(checklist.components)

        return persistence.update(checklist)
    }

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(UpdateContractingChecklistInstanceStatusException::class)
    override fun changeStatus(projectId: Long, checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance {
        val existing = persistence.getChecklistSummary(checklistId, ProgrammeChecklistType.CONTRACTING, projectId)

        val isReturnToDraft = existing.status == ChecklistInstanceStatus.FINISHED
                && status == ChecklistInstanceStatus.DRAFT

        val userCanFinish = existing.status == ChecklistInstanceStatus.DRAFT
                && status == ChecklistInstanceStatus.FINISHED
                && userAuthorization.getUser().email == existing.creatorEmail

        if (!isReturnToDraft && !userCanFinish)
            throw UpdateContractingChecklistInstanceStatusNotAllowedException()

        return persistence.changeStatus(checklistId, status).also {
            auditPublisher.publishEvent(
                projectContractingChecklistStatusChanged(
                    context = this,
                    checklist = it,
                    oldStatus = existing.status
                )
            )
        }
    }

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(UpdateContractingChecklistInstanceException::class)
    override fun updateContractingChecklistDescription(projectId: Long, checklistId: Long, description: String?): ChecklistInstance {
        val checklist = persistence.getChecklistSummary(checklistId)
        if (checklist.relatedToId != projectId || checklist.type != ProgrammeChecklistType.CONTRACTING)
            throw UpdateContractingChecklistInstanceNotFoundException()
        return persistence.updateDescription(checklistId, description)
    }
}
