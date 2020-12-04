package io.cloudflight.jems.api.project.dto.description

import io.cloudflight.jems.api.project.dto.InputTranslation

data class InputProjectPartnership(

    val partnership: Set<InputTranslation> = emptySet()

)
