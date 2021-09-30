package io.cloudflight.jems.server.project.entity.partner.budget

import java.math.BigDecimal

data class ProjectPartnerBudgetPerPeriodRowImpl(

    override val id: Long,
    override val periodNumber: Int?,
    override val staffCostsPerPeriod: BigDecimal?,
    override val travelAndAccommodationCostsPerPeriod: BigDecimal?,
    override val equipmentCostsPerPeriod: BigDecimal?,
    override val externalExpertiseAndServicesCostsPerPeriod: BigDecimal?,
    override val infrastructureAndWorksCostsPerPeriod: BigDecimal?,
    override val unitCostsPerPeriod: BigDecimal?

) : ProjectPartnerBudgetPerPeriodRow
