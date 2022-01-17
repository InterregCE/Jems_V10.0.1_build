package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal

interface ProjectPartnerBudgetPerPeriodRow {

    val id: Long
    val periodNumber: Int?
    val staffCostsPerPeriod: BigDecimal?
    val travelAndAccommodationCostsPerPeriod: BigDecimal?
    val equipmentCostsPerPeriod: BigDecimal?
    val externalExpertiseAndServicesCostsPerPeriod: BigDecimal?
    val infrastructureAndWorksCostsPerPeriod: BigDecimal?
    val unitCostsPerPeriod: BigDecimal?

}
