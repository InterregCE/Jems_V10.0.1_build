package io.cloudflight.jems.api.project.dto.report.project.verification.expenditure

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.BudgetCategoryDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ExpenditureParkingMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureInvestmentBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureLumpSumBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureUnitCostBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementDTO
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportExpenditureItemDTO(

    val id: Long,
    val number: Int,

    val partnerId: Long,
    val partnerRole: ProjectPartnerRoleDTO,
    val partnerNumber: Int,

    val partnerReportId: Long,
    val partnerReportNumber: Int,

    var lumpSum: ExpenditureLumpSumBreakdownLineDTO?,
    val unitCost: ExpenditureUnitCostBreakdownLineDTO?,
    var gdpr: Boolean,
    val costCategory: BudgetCategoryDTO,
    val investment: ExpenditureInvestmentBreakdownLineDTO?,
    val contract: ProjectPartnerReportProcurementDTO?,
    val internalReferenceNumber: String?,
    val invoiceNumber: String?,
    val invoiceDate: LocalDate?,
    val dateOfPayment: LocalDate?,
    var description: Set<InputTranslation> = emptySet(),
    var comment: Set<InputTranslation> = emptySet(),
    val totalValueInvoice: BigDecimal? = null,
    val vat: BigDecimal? = null,
    val numberOfUnits: BigDecimal = BigDecimal.ONE,
    val pricePerUnit: BigDecimal = BigDecimal.ZERO,
    val declaredAmount: BigDecimal = BigDecimal.ZERO,
    val currencyCode: String,
    val currencyConversionRate: BigDecimal?,
    var declaredAmountAfterSubmission: BigDecimal?,
    val attachment: JemsFileMetadataDTO?,

    var partOfSample: Boolean,
    var partOfSampleLocked: Boolean,
    var certifiedAmount: BigDecimal,
    var deductedAmount: BigDecimal,
    var typologyOfErrorId: Long?,
    var parked: Boolean,
    var verificationComment: String?,

    val parkingMetadata: ExpenditureParkingMetadataDTO?,
)
