package io.cloudflight.jems.server.project.entity.partner.budget.staff_cost

import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface ProjectPartnerBudgetStaffCostRow: TranslationView {
    val id: Long
    val partnerId: Long
    val periodNumber: Int?
    val amount: BigDecimal
    val numberOfUnits: BigDecimal
    val pricePerUnit: BigDecimal
    val rowSum: BigDecimal
    val unitCostId: Long?
    val description: String?
    val comment: String?
    val unitType: String?
}
