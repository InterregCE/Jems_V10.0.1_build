package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview

import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import java.math.BigDecimal

data class ExpenditureVerification(
    override val id: Long?,
    override var lumpSumId: Long?,
    override val costCategory: ReportBudgetCategory,
    override var declaredAmountAfterSubmission: BigDecimal?,
    override val parkingMetadata: ExpenditureParkingMetadata?,

    val partOfSample: Boolean,
    val amountAfterVerification: BigDecimal,
    val certifiedAmount: BigDecimal,
    val parked: Boolean,
    val deductedByJs: BigDecimal,
    val deductedByMa: BigDecimal,
): ExpenditureCost
