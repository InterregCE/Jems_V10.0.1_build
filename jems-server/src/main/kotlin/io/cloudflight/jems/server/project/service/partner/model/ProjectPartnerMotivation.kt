package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerMotivation(
    val organizationRelevance: Set<InputTranslation> = emptySet(),
    val organizationRole: Set<InputTranslation> = emptySet(),
    val organizationExperience: Set<InputTranslation> = emptySet()
)
