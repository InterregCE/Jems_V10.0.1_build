package io.cloudflight.jems.server.project.service.report.model.expenditure

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

data class ProjectPartnerReportExpenditureCost(
    val id: Long?,
    val costCategory: BudgetCategory,
    var investmentId: Long?,
    var contractId: Long?,
    val internalReferenceNumber: String?,
    val invoiceNumber: String?,
    val invoiceDate: LocalDate?,
    val dateOfPayment: LocalDate?,
    val description: Set<InputTranslation> = emptySet(),
    val comment: Set<InputTranslation> = emptySet(),
    val totalValueInvoice: BigDecimal? = null,
    val vat: BigDecimal? = null,
    val declaredAmount: BigDecimal,
    var currencyCode: String,
    var currencyConversionRate: BigDecimal?,
    var declaredAmountAfterSubmission: BigDecimal?,
) {
    fun clearConversions() {
        currencyConversionRate = null
        declaredAmountAfterSubmission = null
    }

    fun fillInRate(rate: BigDecimal) {
        currencyConversionRate = rate
        declaredAmountAfterSubmission = declaredAmount.divide(rate, 2, RoundingMode.HALF_UP)
    }
}
