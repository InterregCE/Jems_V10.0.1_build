package io.cloudflight.jems.server.project.entity.partner.budget.unit_cost

import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectUnitCostRow: TranslationView {

    val id: Long
    val costId: Long
    val pricePerUnit: Long?
    val numberOfUnits: Long?
    val name: String?
    val description: String?
    val unitType: String?

}