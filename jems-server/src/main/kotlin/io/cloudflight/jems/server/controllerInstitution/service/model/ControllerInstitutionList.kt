package io.cloudflight.jems.server.controllerInstitution.service.model

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import java.time.ZonedDateTime

data class ControllerInstitutionList (
    val id: Long,
    val name: String,
    val description: String?,
    val institutionNuts: List<OutputNuts>,
    val createdAt: ZonedDateTime?
)
