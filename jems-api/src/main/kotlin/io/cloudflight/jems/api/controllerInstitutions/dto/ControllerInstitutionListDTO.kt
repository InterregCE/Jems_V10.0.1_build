package io.cloudflight.jems.api.controllerInstitutions.dto

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import java.time.ZonedDateTime

data class ControllerInstitutionListDTO (
    val id: Long?,
    val name: String,
    val description: String?,
    val institutionNuts: List<OutputNuts> = emptyList(),
    val createdAt: ZonedDateTime?
)
