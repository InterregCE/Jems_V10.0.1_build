package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportExpenditureCostOld(
    val id: Long,
    val number: Int,
    val lumpSumId: Long?,
    val unitCostId: Long?,
    val gdpr: Boolean,
    val category: ReportBudgetCategory,
    val investmentId: Long?,
    val procurementId: Long?,
    val internalReferenceNumber: String?,
    val invoiceNumber: String?,
    val invoiceDate: LocalDate?,
    val dateOfPayment: LocalDate?,
    val description: Set<InputTranslation> = emptySet(),
    val comment: Set<InputTranslation> = emptySet(),
    val totalValueInvoice: BigDecimal? = null,
    val vat: BigDecimal? = null,
    val numberOfUnits: BigDecimal,
    val pricePerUnit: BigDecimal,
    val declaredAmount: BigDecimal,
    val currencyCode: String,
    val currencyConversionRate: BigDecimal?,
    val declaredAmountAfterSubmission: BigDecimal?,
    val parkingMetadata: ExpenditureParkingMetadata?,
) {
    fun isNotReIncluded() = parkingMetadata == null
}
