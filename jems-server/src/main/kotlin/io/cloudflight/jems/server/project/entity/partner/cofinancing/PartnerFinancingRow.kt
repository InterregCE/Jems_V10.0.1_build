package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundType
import io.cloudflight.jems.server.common.entity.TranslationView
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import java.math.BigDecimal

interface PartnerFinancingRow: TranslationView {
    val partnerId: Long
    val type: ProjectPartnerCoFinancingFundType
    val percentage: BigDecimal
    val fundId: Long?
    val selected: Boolean?
    val fundType: ProgrammeFundType?
    val abbreviation: String?
    val description: String?
}