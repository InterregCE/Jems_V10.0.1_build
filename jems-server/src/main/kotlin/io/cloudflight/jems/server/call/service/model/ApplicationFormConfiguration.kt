package io.cloudflight.jems.server.call.service.model

data class ApplicationFormConfiguration(
    val id: Long,
    val name: String,
    val fieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>
)
