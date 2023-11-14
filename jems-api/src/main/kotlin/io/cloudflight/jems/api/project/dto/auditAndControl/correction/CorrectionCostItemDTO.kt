package io.cloudflight.jems.api.project.dto.auditAndControl.correction

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.BudgetCategoryDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportLumpSumDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportUnitCostDTO
import java.math.BigDecimal
import java.time.LocalDate

data class CorrectionCostItemDTO(
    val id: Long,
    val number: Int,
    val partnerReportNumber: Int,
    val lumpSum: ProjectPartnerReportLumpSumDTO?,
    val unitCost: ProjectPartnerReportUnitCostDTO?,
    val costCategory: BudgetCategoryDTO,
    val investmentId: Long?,
    val investmentNumber: Int?,
    val investmentWorkPackageNumber: Int?,
    val contractId: Long?,
    val internalReferenceNumber: String?,
    val invoiceNumber: String?,
    val invoiceDate: LocalDate?,
    val description: Set<InputTranslation> = emptySet(),
    val comment: Set<InputTranslation> = emptySet(),
    val declaredAmount: BigDecimal,
    val currencyCode: String,
    val declaredAmountAfterSubmission: BigDecimal?,
)
