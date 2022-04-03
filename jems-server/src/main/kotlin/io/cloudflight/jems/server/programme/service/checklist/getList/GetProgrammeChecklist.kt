package io.cloudflight.jems.server.programme.service.checklist.getList

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklist
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
) : GetProgrammeChecklistInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProgrammeChecklistException::class)
    override fun getProgrammeChecklist(): List<ProgrammeChecklist> =
        persistence.getMax100Checklists()
}
