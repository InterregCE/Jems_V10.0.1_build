package io.cloudflight.jems.server.project.service.checklist.delete.contracting

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectChecklistDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteContractingChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : DeleteContractingChecklistInstanceInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(DeleteContractingChecklistInstanceException::class)
    override fun deleteById(projectId: Long, checklistId: Long) {
        val checklistToBeDeleted =
            persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CONTRACTING, projectId)

        if (checklistToBeDeleted.status == ChecklistInstanceStatus.FINISHED)
            throw DeleteContractingChecklistInstanceStatusNotAllowedException()

        persistence.deleteById(checklistId).also {
            auditPublisher.publishEvent(
                projectChecklistDeleted(
                    context = this,
                    checklist = checklistToBeDeleted,
                    projectId = projectId,
                    partner = null,
                    reportId = null
                )
            )
        }
    }
}
