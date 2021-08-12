package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface PartnerFinancingRow: TranslationView {
    val order_nr: Int
    val percentage: BigDecimal
    val fundId: Long?
    val selected: Boolean?
    val fundType: String?
    val abbreviation: String?
    val description: String?
}
