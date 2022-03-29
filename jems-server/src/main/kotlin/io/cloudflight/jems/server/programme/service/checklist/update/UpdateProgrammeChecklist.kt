package io.cloudflight.jems.server.programme.service.checklist.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProgrammeChecklist(
    private val persistence: ProgrammeChecklistPersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateProgrammeChecklistInteractor {

    companion object {
        const val MAX_NUMBER_OF_CHECKLIST_COMPONENTS = 100
    }

    @Transactional
    @CanUpdateProgrammeSetup
    @ExceptionWrapper(UpdateProgrammeChecklistException::class)
    override fun update(programmeChecklist: ProgrammeChecklistDetail): ProgrammeChecklistDetail {
        validateInput(programmeChecklist)
        return persistence.createOrUpdate(programmeChecklist)
    }

    private fun validateInput(model: ProgrammeChecklistDetail) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.notNullOrZero(model.id, "id"),
            generalValidator.maxSize(
                model.components, MAX_NUMBER_OF_CHECKLIST_COMPONENTS, "components"
            )
        )
}
