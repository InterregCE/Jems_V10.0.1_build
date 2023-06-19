package io.cloudflight.jems.server.project.service.checklist.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanDeleteChecklistAssessment
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.checklistDeleted
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher
) : DeleteChecklistInstanceInteractor {

    @CanDeleteChecklistAssessment
    @Transactional
    @ExceptionWrapper(DeleteChecklistInstanceException::class)
    override fun deleteById(checklistId: Long, projectId: Long) {
        val checklistToBeDeleted =
            persistence.getChecklistDetail(id = checklistId, type = ProgrammeChecklistType.APPLICATION_FORM_ASSESSMENT, relatedToId = projectId)
        if (checklistToBeDeleted.status == ChecklistInstanceStatus.FINISHED)
            throw DeleteChecklistInstanceStatusNotAllowedException()
        persistence.deleteById(checklistId).also {
            auditPublisher.publishEvent(
                checklistDeleted(
                    context = this,
                    checklist = checklistToBeDeleted
                )
            )
        }
    }
}
