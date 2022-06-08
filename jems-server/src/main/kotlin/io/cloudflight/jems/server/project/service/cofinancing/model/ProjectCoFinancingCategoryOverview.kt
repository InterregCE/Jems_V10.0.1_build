package io.cloudflight.jems.server.project.service.cofinancing.model

import java.math.BigDecimal

data class ProjectCoFinancingCategoryOverview(
    val fundOverviews: List<ProjectCoFinancingByFundOverview> = emptyList(),

    val totalFundingAmount: BigDecimal = BigDecimal.ZERO,
    val totalEuFundingAmount: BigDecimal = BigDecimal.ZERO,
    val averageCoFinancingRate: BigDecimal = BigDecimal.ZERO,
    val averageEuFinancingRate: BigDecimal = BigDecimal.ZERO,

    val totalAutoPublicContribution: BigDecimal = BigDecimal.ZERO,
    val totalEuAutoPublicContribution: BigDecimal = BigDecimal.ZERO,
    val totalOtherPublicContribution: BigDecimal = BigDecimal.ZERO,
    val totalEuOtherPublicContribution: BigDecimal = BigDecimal.ZERO,
    val totalPublicContribution: BigDecimal = BigDecimal.ZERO,
    val totalEuPublicContribution: BigDecimal = BigDecimal.ZERO,
    val totalPrivateContribution: BigDecimal = BigDecimal.ZERO,
    val totalEuPrivateContribution: BigDecimal = BigDecimal.ZERO,
    val totalContribution: BigDecimal = BigDecimal.ZERO,
    val totalEuContribution: BigDecimal = BigDecimal.ZERO,

    val totalFundAndContribution: BigDecimal = BigDecimal.ZERO,
    val totalEuFundAndContribution: BigDecimal = BigDecimal.ZERO
)
