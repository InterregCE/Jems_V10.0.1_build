package io.cloudflight.jems.server.programme.service.indicator.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import java.math.BigDecimal

data class OutputIndicator(
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: String,
    val resultIndicatorId: Long?,
    val programmeObjectivePolicy: ProgrammeObjectivePolicy?,
    val measurementUnit: String?,
    val milestone: BigDecimal?,
    val finalTarget: BigDecimal?
)
