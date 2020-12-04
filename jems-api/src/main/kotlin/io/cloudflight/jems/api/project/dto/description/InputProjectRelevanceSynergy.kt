package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.project.dto.InputTranslation

data class InputProjectRelevanceSynergy(

    val synergy: Set<InputTranslation> = emptySet(),
    val specification: Set<InputTranslation> = emptySet()

)
