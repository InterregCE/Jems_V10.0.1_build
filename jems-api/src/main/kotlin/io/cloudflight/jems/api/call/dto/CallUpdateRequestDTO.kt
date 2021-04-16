package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.time.ZonedDateTime

data class CallUpdateRequestDTO (
    val id: Long? = null,
    val name: String,
    val startDateTime: ZonedDateTime,
    val endDateTimeStep1: ZonedDateTime? = null,
    val endDateTime: ZonedDateTime,
    val isAdditionalFundAllowed: Boolean,
    val lengthOfPeriod: Int,
    val description: Set<InputTranslation> = emptySet(),
    val priorityPolicies: Set<ProgrammeObjectivePolicy> = emptySet(),
    val strategies: Set<ProgrammeStrategy> = emptySet(),
    val fundIds: Set<Long> = emptySet(),
)
