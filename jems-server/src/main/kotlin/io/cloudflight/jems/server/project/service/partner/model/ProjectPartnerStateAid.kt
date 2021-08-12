package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerStateAid(
    val answer1: Boolean?,
    val justification1: Set<InputTranslation> = emptySet(),
    val answer2: Boolean?,
    val justification2: Set<InputTranslation> = emptySet(),
    val answer3: Boolean?,
    val justification3: Set<InputTranslation> = emptySet(),
    val answer4: Boolean?,
    val justification4: Set<InputTranslation> = emptySet(),
)
