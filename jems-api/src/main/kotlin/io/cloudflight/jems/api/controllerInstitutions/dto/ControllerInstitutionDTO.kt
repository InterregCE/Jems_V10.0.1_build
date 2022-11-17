package io.cloudflight.jems.api.controllerInstitutions.dto

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import java.time.ZonedDateTime

data class ControllerInstitutionDTO(
    val id: Long?,
    val name: String,
    val description: String?,
    val institutionNuts: List<OutputNuts> = emptyList(),
    val institutionUsers: List<ControllerInstitutionUserDTO> = emptyList(),
    val createdAt: ZonedDateTime?
)
