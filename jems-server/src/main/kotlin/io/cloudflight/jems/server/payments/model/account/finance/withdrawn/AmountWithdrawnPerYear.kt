package io.cloudflight.jems.server.payments.model.account.finance.withdrawn

import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import java.math.BigDecimal

data class AmountWithdrawnPerYear(
    val year: AccountingYear,

    val withdrawalTotal: BigDecimal,
    val withdrawalPublic: BigDecimal,

    val withdrawalTotalOfWhichAa: BigDecimal,
    val withdrawalPublicOfWhichAa: BigDecimal,

    val withdrawalTotalOfWhichEc: BigDecimal,
    val withdrawalPublicOfWhichEc: BigDecimal,
)
