package io.cloudflight.jems.server.project.service.checklist.delete.contracting

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectContractingChecklistDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteContractingChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService
) : DeleteContractingChecklistInstanceInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(DeleteContractingChecklistInstanceException::class)
    override fun deleteById(projectId: Long, checklistId: Long) {
        val checklist = persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CONTRACTING, projectId)

        if (checklist.status == ChecklistInstanceStatus.FINISHED || checklist.creatorId != securityService.getUserIdOrThrow())
            throw DeleteContractingChecklistInstanceNotAllowedException()

        persistence.deleteById(checklistId).also {
            auditPublisher.publishEvent(
                projectContractingChecklistDeleted(
                    context = this,
                    checklist = checklist,
                    projectId = projectId
                )
            )
        }
    }
}
