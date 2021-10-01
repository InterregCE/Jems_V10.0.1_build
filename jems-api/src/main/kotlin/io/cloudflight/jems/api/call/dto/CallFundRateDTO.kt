package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal
import java.time.ZonedDateTime

data class CallFundRateDTO(
    val programmeFund: ProgrammeFundDTO,
    val rate: BigDecimal,
    val adjustable: Boolean
)
