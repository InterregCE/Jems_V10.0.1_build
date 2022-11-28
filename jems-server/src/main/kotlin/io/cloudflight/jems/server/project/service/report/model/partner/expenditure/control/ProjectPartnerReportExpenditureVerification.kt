package io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportExpenditureVerification(
    val id: Long,
    val lumpSumId: Long?,
    val unitCostId: Long?,
    val costCategory: ReportBudgetCategory,
    val investmentId: Long?,
    val contractId: Long?,
    val internalReferenceNumber: String?,
    val invoiceNumber: String?,
    val invoiceDate: LocalDate?,
    val dateOfPayment: LocalDate?,
    val description: Set<InputTranslation> = emptySet(),
    val comment: Set<InputTranslation> = emptySet(),
    val totalValueInvoice: BigDecimal? = null,
    val vat: BigDecimal? = null,
    val numberOfUnits: BigDecimal = BigDecimal.ONE,
    val pricePerUnit: BigDecimal = BigDecimal.ZERO,
    val declaredAmount: BigDecimal = BigDecimal.ZERO,
    val currencyCode: String,
    val currencyConversionRate: BigDecimal?,
    val declaredAmountAfterSubmission: BigDecimal?,
    val attachment: JemsFileMetadata?,

    var partOfSample: Boolean,
    var certifiedAmount: BigDecimal,
    var deductedAmount: BigDecimal,
    var typologyOfErrorId: Long?,
    var verificationComment: String?,
)
