package io.cloudflight.jems.api.call.dto.application_form_configuration

data class ApplicationFormConfigurationDTO(
    val id: Long,
    val name: String,
    val fieldConfigurations: MutableSet<ApplicationFormFieldConfigurationDTO>
)
