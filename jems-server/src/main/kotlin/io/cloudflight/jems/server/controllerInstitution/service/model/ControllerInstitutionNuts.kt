package io.cloudflight.jems.server.controllerInstitution.service.model

import io.cloudflight.jems.api.nuts.dto.OutputNuts

data class ControllerInstitutionNuts (
    val id:Long,
    val title: String,
    val nutsRegion2: OutputNuts
)
