package io.cloudflight.jems.server.project.service.checklist

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistComponentInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistComponentType
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.OptionsToggleInstanceMetadata
import io.cloudflight.jems.server.programme.service.checklist.model.metadata.TextInputMetadata
import io.cloudflight.jems.server.project.service.checklist.model.metadata.TextInputInstanceMetadata
import org.springframework.stereotype.Service

@Service
class ChecklistInstanceValidator(private val validator: GeneralValidatorService) {

    companion object {
        private const val JUSTIFICATION_FIELD_MAX_LENGTH = 5000
    }

    fun validateChecklistComponents(components: List<ChecklistComponentInstance>?) =
        components?.forEach { component -> validateComponent(component) }


    fun validateComponent(component: ChecklistComponentInstance) {
        when(component.type) {
            ProgrammeChecklistComponentType.TEXT_INPUT -> validateTextInputComponent(component)
            ProgrammeChecklistComponentType.OPTIONS_TOGGLE -> validateOptionsToggleComponent(component)
            else -> return
        }
    }

    fun validateTextInputComponent(component: ChecklistComponentInstance){
        validator.throwIfAnyIsInvalid(
            validator.maxLength(
                (component.instanceMetadata as TextInputInstanceMetadata).explanation,
                (component.programmeMetadata as TextInputMetadata).explanationMaxLength,
                "explanation"
            )
        )
    }

    fun validateOptionsToggleComponent(component: ChecklistComponentInstance) {
        validator.throwIfAnyIsInvalid(
            validator.maxLength(
                (component.instanceMetadata as OptionsToggleInstanceMetadata).justification,
                JUSTIFICATION_FIELD_MAX_LENGTH,
                "justification"
            )
        )
    }
}
