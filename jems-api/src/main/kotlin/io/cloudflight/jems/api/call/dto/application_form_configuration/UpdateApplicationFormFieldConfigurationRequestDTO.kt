package io.cloudflight.jems.api.call.dto.application_form_configuration

data class UpdateApplicationFormFieldConfigurationRequestDTO(
    val id: String,
    val isVisible: Boolean,
    val availableInStep: StepSelectionOptionDTO,
)
