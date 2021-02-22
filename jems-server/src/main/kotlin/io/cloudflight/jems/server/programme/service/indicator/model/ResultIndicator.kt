package io.cloudflight.jems.server.programme.service.indicator.model

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import java.math.BigDecimal

data class ResultIndicator(
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: String,
    val programmeObjectivePolicy: ProgrammeObjectivePolicy?,
    val measurementUnit: String?,
    val baseline: BigDecimal?,
    val referenceYear: String?,
    val finalTarget: BigDecimal?,
    val sourceOfData: String?,
    val comment: String?
)
