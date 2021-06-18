package io.cloudflight.jems.api.call.dto.application_form_configuration

data class ApplicationFormFieldConfigurationDTO(
    val id: String,
    val visibilityStatus: FieldVisibilityStatusDTO,
    val validVisibilityStatusSet: MutableSet<FieldVisibilityStatusDTO> = mutableSetOf()
)
