package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.ZonedDateTime

data class CallUpdateRequestDTO(
    val id: Long? = null,
    val name: String,
    val type: CallType,
    val startDateTime: ZonedDateTime,
    val endDateTimeStep1: ZonedDateTime? = null,
    val endDateTime: ZonedDateTime,
    val additionalFundAllowed: Boolean,
    val lengthOfPeriod: Int,
    val description: Set<InputTranslation> = emptySet(),
    val priorityPolicies: Set<ProgrammeObjectivePolicy> = emptySet(),
    val strategies: Set<ProgrammeStrategy> = emptySet(),
    val funds: Set<CallFundRateDTO> = emptySet(),
    val stateAidIds: Set<Long> = emptySet(),
)
