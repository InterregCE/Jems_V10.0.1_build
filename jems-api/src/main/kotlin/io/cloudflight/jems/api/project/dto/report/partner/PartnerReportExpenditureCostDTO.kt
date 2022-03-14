package io.cloudflight.jems.api.project.dto.report.partner

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal
import java.time.LocalDate

data class PartnerReportExpenditureCostDTO(
    val id: Long?,
    val costCategory: String,
    val investmentNumber: String?,
    val contractId: String?,
    val internalReferenceNumber: String?,
    val invoiceNumber: String?,
    val invoiceDate: LocalDate?,
    val dateOfPayment: LocalDate?,
    val description: Set<InputTranslation> = emptySet(),
    val comment: Set<InputTranslation> = emptySet(),
    val totalValueInvoice: BigDecimal? = null,
    val vat: BigDecimal? = null,
    val declaredAmount: BigDecimal? = null
)
