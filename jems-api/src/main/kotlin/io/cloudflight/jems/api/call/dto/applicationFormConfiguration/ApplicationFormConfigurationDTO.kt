package io.cloudflight.jems.api.call.dto.applicationFormConfiguration

data class ApplicationFormConfigurationDTO(
    val id: Long,
    val name: String,
    val fieldConfigurations: MutableSet<ApplicationFormFieldConfigurationDTO>
)
