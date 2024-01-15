package io.cloudflight.jems.server.payments.model.ec

import java.math.BigDecimal
import java.time.LocalDate

data class PaymentApplicationToEcCreate(
    val id: Long?,
    val programmeFundId: Long,
    val accountingYearId: Long,
    val nationalReference: String?,
    val technicalAssistanceEur: BigDecimal,
    val submissionToSfcDate: LocalDate?,
    val sfcNumber: String?,
    val comment: String?
)