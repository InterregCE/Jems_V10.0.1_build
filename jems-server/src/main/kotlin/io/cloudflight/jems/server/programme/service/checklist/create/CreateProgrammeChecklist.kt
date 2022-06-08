package io.cloudflight.jems.server.programme.service.checklist.create

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ChecklistTemplateValidator
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
    private val checklistTemplateValidator: ChecklistTemplateValidator,
) : CreateProgrammeChecklistInteractor {

    companion object {
        const val MAX_NUMBER_OF_CHECKLIST = 100
        const val MAX_NUMBER_OF_CHECKLIST_COMPONENTS = 100
    }

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(CreateProgrammeChecklistException::class)
    override fun create(programmeChecklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail {
        checklistTemplateValidator.validateNewChecklist(programmeChecklist)
        if (persistence.countAll().toInt() >= MAX_NUMBER_OF_CHECKLIST)
            throw MaxAmountOfProgrammeChecklistReached(maxAmount = MAX_NUMBER_OF_CHECKLIST)
        return persistence.createOrUpdate(programmeChecklist)
    }
}
