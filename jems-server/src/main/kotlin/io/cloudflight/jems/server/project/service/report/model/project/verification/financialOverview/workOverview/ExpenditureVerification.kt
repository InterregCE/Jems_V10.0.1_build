package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCostAfterControl
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import java.math.BigDecimal

data class ExpenditureVerification(
    override val id: Long?,
    override var lumpSumId: Long?,
    val lumpSumOrderNr: Int?,
    val unitCostId: Long?,
    val investmentId: Long?,
    override val costCategory: ReportBudgetCategory,
    override var declaredAmountAfterSubmission: BigDecimal?,
    override val parkingMetadata: ExpenditureParkingMetadata?,

    val partOfSample: Boolean,
    val amountAfterVerification: BigDecimal,
    override val certifiedAmount: BigDecimal,
    val parked: Boolean,
    val deductedByJs: BigDecimal,
    val deductedByMa: BigDecimal,
    val typologyOfErrorId: Long?
): ExpenditureCostAfterControl
