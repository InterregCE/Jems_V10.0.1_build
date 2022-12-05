package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import java.math.BigDecimal

interface ExpenditureCost {
    val id: Long?
    var lumpSumId: Long?
    val costCategory: ReportBudgetCategory
    var declaredAmountAfterSubmission: BigDecimal?
}
