package io.cloudflight.jems.server.programme.service.checklist.create

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
    private val generalValidator: GeneralValidatorService,
) : CreateProgrammeChecklistInteractor {

    companion object {
        const val MAX_NUMBER_OF_CHECKLIST = 100
        const val MAX_NUMBER_OF_CHECKLIST_COMPONENTS = 100
    }

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(CreateProgrammeChecklistException::class)
    override fun create(programmeChecklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail {
        validateInput(programmeChecklist)
        if (persistence.countAll().toInt() >= MAX_NUMBER_OF_CHECKLIST)
            throw MaxAmountOfProgrammeChecklistReached(maxAmount = MAX_NUMBER_OF_CHECKLIST)
        return persistence.createOrUpdate(programmeChecklist)
    }

    private fun validateInput(model: ProgrammeChecklistDetail) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.nullOrZero(model.id, "id"),
            generalValidator.maxSize(model.components, MAX_NUMBER_OF_CHECKLIST_COMPONENTS, "components")
        )
}
