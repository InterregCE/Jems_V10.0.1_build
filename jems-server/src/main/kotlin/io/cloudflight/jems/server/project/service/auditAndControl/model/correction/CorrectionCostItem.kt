package io.cloudflight.jems.server.project.service.auditAndControl.model.correction

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import java.math.BigDecimal
import java.time.LocalDate

data class CorrectionCostItem(
    val id: Long,
    val number: Int,
    val partnerReportNumber: Int,
    val lumpSum: ProjectPartnerReportLumpSum?,
    val unitCost: ProjectPartnerReportUnitCost?,
    val costCategory: ReportBudgetCategory,
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
