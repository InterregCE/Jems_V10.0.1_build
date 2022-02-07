package io.cloudflight.jems.api.call.dto

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.math.BigDecimal

data class CallFundRateDTO(
    val programmeFund: ProgrammeFundDTO,
    val rate: BigDecimal,
    val adjustable: Boolean
)
