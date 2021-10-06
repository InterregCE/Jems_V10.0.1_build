package io.cloudflight.jems.server.project.service.budget.model

import java.math.BigDecimal

data class ProjectPartnerBudget(

    val id: Long,
    val periodNumber: Int = 0,
    val staffCostsPerPeriod: BigDecimal = BigDecimal.ZERO,
    val travelAndAccommodationCostsPerPeriod: BigDecimal = BigDecimal.ZERO,
    val equipmentCostsPerPeriod: BigDecimal = BigDecimal.ZERO,
    val externalExpertiseAndServicesCostsPerPeriod: BigDecimal = BigDecimal.ZERO,
    val infrastructureAndWorksCostsPerPeriod: BigDecimal = BigDecimal.ZERO,
    val unitCostsPerPeriod: BigDecimal = BigDecimal.ZERO,

)
