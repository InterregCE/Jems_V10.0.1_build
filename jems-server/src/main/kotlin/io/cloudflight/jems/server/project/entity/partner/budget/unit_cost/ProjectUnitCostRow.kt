package io.cloudflight.jems.server.project.entity.partner.budget.unit_cost

import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface ProjectUnitCostRow: TranslationView {

    val id: Long
    val costId: Long
    val pricePerUnit: BigDecimal?
    val numberOfUnits: BigDecimal?
    val name: String?
    val description: String?
    val unitType: String?

}