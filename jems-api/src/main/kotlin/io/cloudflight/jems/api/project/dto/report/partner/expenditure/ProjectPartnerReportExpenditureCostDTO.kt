package io.cloudflight.jems.api.project.dto.report.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportExpenditureCostDTO(
    val id: Long?,
    val lumpSumId: Long?,
    val unitCostId: Long?,
    val costCategory: BudgetCategoryDTO,
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
    val numberOfUnits: BigDecimal,
    val pricePerUnit: BigDecimal,
    val declaredAmount: BigDecimal = BigDecimal.ZERO,
    val currencyCode: String,
    val currencyConversionRate: BigDecimal?,
    val declaredAmountAfterSubmission: BigDecimal?,
    val attachment: ProjectReportFileMetadataDTO?,
)
