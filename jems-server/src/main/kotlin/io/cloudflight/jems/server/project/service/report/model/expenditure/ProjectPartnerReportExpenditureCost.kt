package io.cloudflight.jems.server.project.service.report.model.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

data class ProjectPartnerReportExpenditureCost(
    val id: Long?,
    var lumpSumId: Long?,
    var unitCostId: Long?,
    var costCategory: ReportBudgetCategory,
    var investmentId: Long?,
    var contractId: Long?,
    var internalReferenceNumber: String?,
    var invoiceNumber: String?,
    var invoiceDate: LocalDate?,
    var dateOfPayment: LocalDate?,
    val description: Set<InputTranslation> = emptySet(),
    val comment: Set<InputTranslation> = emptySet(),
    var totalValueInvoice: BigDecimal? = null,
    var vat: BigDecimal? = null,
    var numberOfUnits: BigDecimal,
    var pricePerUnit: BigDecimal,
    var declaredAmount: BigDecimal,
    var currencyCode: String,
    var currencyConversionRate: BigDecimal?,
    var declaredAmountAfterSubmission: BigDecimal?,
    val attachment: ProjectReportFileMetadata?,
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
