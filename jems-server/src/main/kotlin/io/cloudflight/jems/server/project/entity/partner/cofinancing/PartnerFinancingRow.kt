package io.cloudflight.jems.server.project.entity.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundType
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import java.math.BigDecimal

interface PartnerFinancingRow {
    val partnerId: Long
    val type: ProjectPartnerCoFinancingFundType
    val percentage: BigDecimal
    val programmeFund: ProgrammeFundEntity?
}