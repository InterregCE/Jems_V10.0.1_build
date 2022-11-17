package io.cloudflight.jems.server.controllerInstitution.service.model

import java.time.ZonedDateTime

data class UpdateControllerInstitution (
    val id: Long,
    val name: String,
    val description: String?,
    val institutionNuts: Collection<String>,
    val institutionUsers: List<ControllerInstitutionUser>,
    val createdAt: ZonedDateTime?
)
