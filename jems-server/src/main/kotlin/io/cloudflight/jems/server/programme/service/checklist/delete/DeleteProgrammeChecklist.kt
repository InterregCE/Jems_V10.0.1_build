package io.cloudflight.jems.server.programme.service.checklist.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence
) : DeleteProgrammeChecklistInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(DeleteProgrammeChecklistException::class)
    override fun deleteProgrammeChecklist(programmeChecklistId: Long) {
        persistence.deleteById(programmeChecklistId)
    }
}
