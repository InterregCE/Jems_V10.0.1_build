package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

data class ProjectPartnerReportExpenditureCost(
    override val id: Long?,
    var number: Int,
    override var lumpSumId: Long?,
    var unitCostId: Long?,
    var gdpr: Boolean,
    override var costCategory: ReportBudgetCategory,
    var investmentId: Long?,
    var contractId: Long?,
    var internalReferenceNumber: String?,
    var invoiceNumber: String?,
    var invoiceDate: LocalDate?,
    var dateOfPayment: LocalDate?,
    var description: Set<InputTranslation> = emptySet(),
    var comment: Set<InputTranslation> = emptySet(),
    var totalValueInvoice: BigDecimal? = null,
    var vat: BigDecimal? = null,
    var numberOfUnits: BigDecimal,
    var pricePerUnit: BigDecimal,
    var declaredAmount: BigDecimal,
    var currencyCode: String,
    var currencyConversionRate: BigDecimal?,
    override var declaredAmountAfterSubmission: BigDecimal?,
    val attachment: JemsFileMetadata?,
    override var parkingMetadata: ExpenditureParkingMetadata?,
): ExpenditureCost {
    fun clearConversions() {
        currencyConversionRate = null
        declaredAmountAfterSubmission = null
    }

    fun fillInRate(rate: BigDecimal) {
        currencyConversionRate = rate
        declaredAmountAfterSubmission = declaredAmount.divide(rate, 2, RoundingMode.HALF_UP)
    }
}
