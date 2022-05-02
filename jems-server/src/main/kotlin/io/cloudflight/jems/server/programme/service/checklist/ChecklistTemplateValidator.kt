package io.cloudflight.jems.server.programme.service.checklist

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponent
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistDetail
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.HeadlineMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
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

    fun validateCheckListComponents(component: ProgrammeChecklistComponent) {
        when (component.type) {
            ProgrammeChecklistComponentType.HEADLINE -> validator.throwIfAnyIsInvalid(
                validator.maxLength(
                    (component.metadata as HeadlineMetadata).value,
                    200,
                    "headline"
                )
            )
            ProgrammeChecklistComponentType.OPTIONS_TOGGLE -> validator.throwIfAnyIsInvalid(
                validator.maxLength(
                    (component.metadata as OptionsToggleMetadata).question,
                    1000,
                    "question"
                ),
                validator.maxLength(
                    (component.metadata as OptionsToggleMetadata).firstOption,
                    100,
                    "firstOption"
                ),
                validator.maxLength(
                    (component.metadata as OptionsToggleMetadata).secondOption,
                    100,
                    "secondOption"
                )
            )
            ProgrammeChecklistComponentType.TEXT_INPUT -> validator.throwIfAnyIsInvalid(
                validator.maxLength(
                    (component.metadata as TextInputMetadata).question,
                    1000,
                    "question"
                ),
                validator.maxLength(
                    (component.metadata as TextInputMetadata).explanationLabel,
                    50,
                    "explanationLabel"
                ),
            )
        }
    }

}
