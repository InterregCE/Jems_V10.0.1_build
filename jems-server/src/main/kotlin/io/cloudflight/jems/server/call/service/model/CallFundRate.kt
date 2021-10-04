package io.cloudflight.jems.server.call.service.model

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal

data class CallFundRate(
    val programmeFund: ProgrammeFund,
    val rate: BigDecimal,
    val adjustable: Boolean
) : Comparable<CallFundRate> {

    override fun compareTo(other: CallFundRate): Int = programmeFund.id.compareTo(other.programmeFund.id)

}
