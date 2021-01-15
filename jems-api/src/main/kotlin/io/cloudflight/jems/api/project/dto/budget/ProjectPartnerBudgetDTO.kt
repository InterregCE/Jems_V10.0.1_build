package io.cloudflight.jems.api.project.dto.budget

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import java.math.BigDecimal

data class ProjectPartnerBudgetDTO(
    val partner: OutputProjectPartner? = null,

    val staffCosts: BigDecimal = BigDecimal.ZERO,
    val travelCosts: BigDecimal = BigDecimal.ZERO,
    val externalCosts: BigDecimal = BigDecimal.ZERO,
    val equipmentCosts: BigDecimal = BigDecimal.ZERO,
    val infrastructureCosts: BigDecimal = BigDecimal.ZERO,

    val officeAndAdministrationCosts: BigDecimal = BigDecimal.ZERO,
    val otherCosts: BigDecimal = BigDecimal.ZERO,
    val totalSum: BigDecimal = BigDecimal.ZERO,

    val lumpSumContribution: BigDecimal = BigDecimal.ZERO,
    val unitCosts: BigDecimal = BigDecimal.ZERO,
)
