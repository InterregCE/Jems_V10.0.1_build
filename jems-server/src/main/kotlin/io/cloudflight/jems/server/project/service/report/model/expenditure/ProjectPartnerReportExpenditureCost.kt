package io.cloudflight.jems.server.project.service.report.model.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportExpenditureCost(
    val id: Long?,
    val costCategory: String,
    val investmentNumber: String?,
    val contractId: String?,
    val internalReferenceNumber: String?,
    val invoiceNumber: String?,
    var invoiceDate: LocalDate?,
    var dateOfPayment: LocalDate?,
    var description: Set<InputTranslation> = emptySet(),
    var comment: Set<InputTranslation> = emptySet(),
    val totalValueInvoice: BigDecimal? = null,
    val vat: BigDecimal? = null,
    val declaredAmount: BigDecimal? = null
)
