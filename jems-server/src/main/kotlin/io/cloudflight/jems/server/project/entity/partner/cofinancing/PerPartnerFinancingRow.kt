package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.server.common.entity.TranslationView
import java.math.BigDecimal

interface PerPartnerFinancingRow: TranslationView {
    val partnerId: Long
    val orderNr: Int
    val percentage: BigDecimal
    val fundId: Long?
    val selected: Boolean?
    val fundType: String?
    val abbreviation: String?
    val description: String?
}
