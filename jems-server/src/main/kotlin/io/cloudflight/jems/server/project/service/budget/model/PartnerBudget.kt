package io.cloudflight.jems.server.project.service.budget.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.math.BigDecimal

data class PartnerBudget(
    val partner: ProjectPartnerSummary,
    val staffCosts: BigDecimal = BigDecimal.ZERO,
    val travelCosts: BigDecimal = BigDecimal.ZERO,
    val externalCosts: BigDecimal = BigDecimal.ZERO,
    val equipmentCosts: BigDecimal = BigDecimal.ZERO,
    val infrastructureCosts: BigDecimal = BigDecimal.ZERO,
    val officeAndAdministrationCosts: BigDecimal = BigDecimal.ZERO,
    val otherCosts: BigDecimal = BigDecimal.ZERO,
    val lumpSumContribution: BigDecimal = BigDecimal.ZERO,
    val unitCosts: BigDecimal = BigDecimal.ZERO,
    val spfCosts: BigDecimal = BigDecimal.ZERO,
    val totalCosts: BigDecimal = BigDecimal.ZERO,
) {
    fun totalBudgetWithoutSpf() = totalCosts.minus(spfCosts)
}
