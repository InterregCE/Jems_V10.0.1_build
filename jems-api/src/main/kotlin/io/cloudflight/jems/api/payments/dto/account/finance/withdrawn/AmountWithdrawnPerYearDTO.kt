package io.cloudflight.jems.api.payments.dto.account.finance.withdrawn

import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import java.math.BigDecimal

data class AmountWithdrawnPerYearDTO(
    val year: AccountingYearDTO,

    val withdrawalTotal: BigDecimal,
    val withdrawalPublic: BigDecimal,

    val withdrawalTotalOfWhichAa: BigDecimal,
    val withdrawalPublicOfWhichAa: BigDecimal,

    val withdrawalTotalOfWhichEc: BigDecimal,
    val withdrawalPublicOfWhichEc: BigDecimal,
)
