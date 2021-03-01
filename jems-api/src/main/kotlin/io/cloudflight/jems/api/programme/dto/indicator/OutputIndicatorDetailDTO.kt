package io.cloudflight.jems.api.programme.dto.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class OutputIndicatorDetailDTO(
    val id: Long?,
    val identifier: String,
    val code: String?,
    val name: Set<InputTranslation> = emptySet(),
    val programmePriorityPolicySpecificObjective: ProgrammeObjectivePolicy?,
    val programmePriorityPolicyCode: String?,
    val programmePriorityCode: String?,
    val measurementUnit: Set<InputTranslation> = emptySet(),
    val milestone: BigDecimal?,
    val finalTarget: BigDecimal?,
    val resultIndicatorId: Long?,
    val resultIndicatorIdentifier: String?,
)
