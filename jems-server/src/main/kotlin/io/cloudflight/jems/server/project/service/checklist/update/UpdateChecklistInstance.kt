package io.cloudflight.jems.server.project.service.checklist.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.authorization.CanUpdateChecklistAssessment
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstanceValidator
import io.cloudflight.jems.server.project.service.checklist.checklistStatusChanged
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val checklistInstanceValidator: ChecklistInstanceValidator,
    private val userAuthorization: UserAuthorization,
) : UpdateChecklistInstanceInteractor {

    @CanUpdateChecklistAssessment
    @Transactional
    @ExceptionWrapper(UpdateChecklistInstanceException::class)
    override fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        val existing = persistence.getChecklistDetail(checklist.id)

        if (existing.status != checklist.status)
            throw UpdateChecklistInstanceStatusNotAllowedException()

        checklistInstanceValidator.validateChecklistComponents(checklist.components)

        return persistence.update(checklist)
    }

    @CanUpdateChecklistAssessment
    @Transactional
    @ExceptionWrapper(UpdateChecklistInstanceException::class)
    override fun changeStatus(checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance {
        val existing = persistence.getChecklistSummary(checklistId)

        val consolidatorCanReturnToDraft = existing.status == ChecklistInstanceStatus.FINISHED
            && status == ChecklistInstanceStatus.DRAFT
            && this.userAuthorization.hasPermissionForProject(UserRolePermission.ProjectAssessmentChecklistConsolidate, checklistId)
            && !existing.consolidated

        val assessorCanFinish = existing.status == ChecklistInstanceStatus.DRAFT
            && status == ChecklistInstanceStatus.FINISHED
            && userAuthorization.getUser().email == existing.creatorEmail

        if (!consolidatorCanReturnToDraft && !assessorCanFinish)
            throw UpdateChecklistInstanceStatusNotAllowedException()

        return persistence.changeStatus(checklistId, status).also {
            auditPublisher.publishEvent(
                checklistStatusChanged(
                    context = this,
                    checklist = it,
                    oldStatus = existing.status,
                )
            )
        }
    }
}
