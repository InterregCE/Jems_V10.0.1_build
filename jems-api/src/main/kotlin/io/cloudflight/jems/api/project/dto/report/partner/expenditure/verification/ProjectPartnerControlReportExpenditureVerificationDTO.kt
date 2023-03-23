package io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.BudgetCategoryDTO
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerControlReportExpenditureVerificationDTO(
    val id: Long?,
    val number: Int,
    val lumpSumId: Long?,
    val unitCostId: Long?,
    val costCategory: BudgetCategoryDTO,
    val gdpr: Boolean,
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
    val attachment: ProjectReportFileMetadataDTO?,
    val parkingMetadata: ExpenditureParkingMetadataDTO?,

    val partOfSample: Boolean,
    val partOfSampleLocked: Boolean,
    val certifiedAmount: BigDecimal,
    val deductedAmount: BigDecimal,
    val typologyOfErrorId: Long?,
    val parked: Boolean,
    val verificationComment: String?
)
