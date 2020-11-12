package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner

fun ProjectPartner.toOutputProjectPartner() = OutputProjectPartner(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber,
    country = country
)
