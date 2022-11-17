package io.cloudflight.jems.server.programme.service.checklist.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.update.ChecklistLockedException
import io.cloudflight.jems.server.programme.service.checklistDeleted
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
    private val checklistInstancePersistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher
) : DeleteProgrammeChecklistInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(DeleteProgrammeChecklistException::class)
    override fun deleteProgrammeChecklist(programmeChecklistId: Long) {
        val checklistToBeDeleted = persistence.getChecklistDetail(programmeChecklistId)
        checklistInstancePersistence.countAllByChecklistTemplateId(programmeChecklistId)
            .let { checklistInstanceCount ->
                if (checklistInstanceCount > 0)
                    throw ChecklistLockedException()
        }

        persistence.deleteById(programmeChecklistId).also {
            auditPublisher.publishEvent(
                checklistDeleted(
                    context = this,
                    checklist = checklistToBeDeleted
                )
            )
        }
    }
}
