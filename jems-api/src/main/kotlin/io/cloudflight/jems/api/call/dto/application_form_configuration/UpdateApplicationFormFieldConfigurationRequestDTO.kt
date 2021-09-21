package io.cloudflight.jems.api.call.dto.application_form_configuration

data class UpdateApplicationFormFieldConfigurationRequestDTO(
    val id: String,
    val visible: Boolean,
    val availableInStep: StepSelectionOptionDTO,
)
