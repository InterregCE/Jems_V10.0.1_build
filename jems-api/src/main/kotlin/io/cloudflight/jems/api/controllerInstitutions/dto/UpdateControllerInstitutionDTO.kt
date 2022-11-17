package io.cloudflight.jems.api.controllerInstitutions.dto

import java.time.ZonedDateTime

data class UpdateControllerInstitutionDTO(
    val id: Long?,
    val name: String,
    val description: String?,
    val institutionNuts: Collection<String> = emptyList(),
    val institutionUsers: List<ControllerInstitutionUserDTO> = emptyList(),
    val createdAt: ZonedDateTime?
)

