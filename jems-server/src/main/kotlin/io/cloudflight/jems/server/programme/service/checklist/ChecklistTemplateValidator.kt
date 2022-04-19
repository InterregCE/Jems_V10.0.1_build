package io.cloudflight.jems.server.programme.service.checklist

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.update.ChecklistLockedException
import org.springframework.stereotype.Service

@Service
class ChecklistTemplateValidator(private val validator: GeneralValidatorService) {

    companion object {
        const val MAX_NUMBER_OF_CHECKLIST_COMPONENTS = 100
    }

    fun validateNewChecklist(model: ProgrammeChecklistDetail) =
        validator.throwIfAnyIsInvalid(
            validator.nullOrZero(model.id, "id"),
            validator.maxSize(
                model.components, MAX_NUMBER_OF_CHECKLIST_COMPONENTS, "components"
            )
        )

    fun validateInput(model: ProgrammeChecklistDetail) =
        validator.throwIfAnyIsInvalid(
            validator.notNullOrZero(model.id, "id"),
            validator.maxSize(
                model.components, MAX_NUMBER_OF_CHECKLIST_COMPONENTS, "components"
            )
        )

    fun validateChecklistState(checklistLocked: Boolean) {
        if(checklistLocked) {
            throw ChecklistLockedException()
        }
    }
}
