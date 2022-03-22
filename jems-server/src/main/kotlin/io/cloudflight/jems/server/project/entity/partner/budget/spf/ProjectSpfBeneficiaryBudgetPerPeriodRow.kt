package io.cloudflight.jems.server.project.entity.partner.budget.spf

import java.math.BigDecimal

interface ProjectSpfBeneficiaryBudgetPerPeriodRow {
    val periodNumber: Int?
    val spfCostPerPeriod: BigDecimal?
}
