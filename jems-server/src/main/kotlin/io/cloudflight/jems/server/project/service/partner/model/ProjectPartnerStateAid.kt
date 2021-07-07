package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerStateAid(
    val answer1: Boolean? = null,
    val justification1: Set<InputTranslation> = emptySet(),
    val answer2: Boolean? = null,
    val justification2: Set<InputTranslation> = emptySet(),
    val answer3: Boolean? = null,
    val justification3: Set<InputTranslation> = emptySet(),
    val answer4: Boolean? = null,
    val justification4: Set<InputTranslation> = emptySet(),
)
