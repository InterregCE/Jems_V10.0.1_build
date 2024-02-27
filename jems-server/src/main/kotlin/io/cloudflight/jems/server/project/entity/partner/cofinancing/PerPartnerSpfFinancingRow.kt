package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.server.common.entity.TranslationView
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType

interface PerPartnerSpfFinancingRow: TranslationView {
    val partnerId: Long
    val fundId: Long
    val type: String
    val abbreviation: String?
}
