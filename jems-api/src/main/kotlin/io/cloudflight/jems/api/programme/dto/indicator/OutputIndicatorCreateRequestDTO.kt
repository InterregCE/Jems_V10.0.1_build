package io.cloudflight.jems.api.programme.dto.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class OutputIndicatorCreateRequestDTO(

    val identifier: String?,
    val code: String?,
    val name: Set<InputTranslation> = emptySet(),
    val programmeObjectivePolicy: ProgrammeObjectivePolicy?,
    val measurementUnit: Set<InputTranslation> = emptySet(),
    val milestone: BigDecimal?,
    val finalTarget: BigDecimal?,
    val resultIndicatorId: Long?

)
