package io.cloudflight.jems.api.call.dto.application_form_configuration

data class ApplicationFormFieldConfigurationDTO(
    val id: String,
    val isVisible: Boolean,
    val availableInStep: StepSelectionOptionDTO,
    val isVisibilityLocked: Boolean,
    val isStepSelectionLocked: Boolean,
)
