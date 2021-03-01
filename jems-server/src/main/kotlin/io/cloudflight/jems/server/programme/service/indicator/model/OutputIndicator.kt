package io.cloudflight.jems.server.programme.service.indicator.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class OutputIndicator(
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: Set<InputTranslation> = emptySet(),
    val resultIndicatorId: Long?,
    val programmeObjectivePolicy: ProgrammeObjectivePolicy?,
    val measurementUnit: Set<InputTranslation> = emptySet(),
    val milestone: BigDecimal?,
    val finalTarget: BigDecimal?
)
