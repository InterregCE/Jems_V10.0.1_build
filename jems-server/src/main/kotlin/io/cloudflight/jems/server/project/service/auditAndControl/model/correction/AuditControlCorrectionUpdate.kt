package io.cloudflight.jems.server.project.service.auditAndControl.model.correction

import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import java.time.LocalDate

data class AuditControlCorrectionUpdate(
    val followUpOfCorrectionId: Long?,
    val correctionFollowUpType: CorrectionFollowUpType,
    val repaymentFrom: LocalDate?,
    val lateRepaymentTo: LocalDate?,

    val partnerId: Long?,
    val partnerReportId: Long?,
    val lumpSumOrderNr: Int?,
    val programmeFundId: Long,

    val costCategory: BudgetCostCategory?,
    val procurementId: Long?,
    val expenditureId: Long?,
)
