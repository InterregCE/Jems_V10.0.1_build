package io.cloudflight.jems.server.programme.service.checklist.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.update.ChecklistLockedException
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
    private val checklistInstancePersistence: ChecklistInstancePersistence,
) : DeleteProgrammeChecklistInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(DeleteProgrammeChecklistException::class)
    override fun deleteProgrammeChecklist(programmeChecklistId: Long) {
        checklistInstancePersistence.countAllByChecklistTemplateId(programmeChecklistId).let {
            if (it > 0)
                throw ChecklistLockedException()
        }
        persistence.deleteById(programmeChecklistId)
    }
}
