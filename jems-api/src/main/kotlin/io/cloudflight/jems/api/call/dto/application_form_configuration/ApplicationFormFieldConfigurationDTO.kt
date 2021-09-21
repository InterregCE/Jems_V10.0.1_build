package io.cloudflight.jems.api.call.dto.application_form_configuration

data class ApplicationFormFieldConfigurationDTO(
    val id: String,
    val visible: Boolean,
    val availableInStep: StepSelectionOptionDTO,
    val visibilityLocked: Boolean,
    val stepSelectionLocked: Boolean,
)
