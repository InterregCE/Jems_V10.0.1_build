package io.cloudflight.jems.server.payments.model.account.finance.withdrawn

import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import java.math.BigDecimal

data class CorrectionAmountWithdrawn(
    val id: Long,
    val priorityAxis: String,
    val ecPaymentIdWhenFound: Long,
    val yearWhenFound: AccountingYear,
    val controllingBody: ControllingBody,

    val ecPaymentIdWhenIncluded: Long,
    val yearWhenIncluded: AccountingYear,

    val public: BigDecimal,
    val total: BigDecimal,
)
