package io.cloudflight.jems.api.call.dto.applicationFormConfiguration

data class UpdateApplicationFormFieldConfigurationRequestDTO(
    val id: String,
    val visible: Boolean,
    val availableInStep: StepSelectionOptionDTO,
)
