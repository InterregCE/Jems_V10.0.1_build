package io.cloudflight.jems.server.project.service.cofinancing.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import java.math.BigDecimal

data class ProjectCoFinancingByFundOverview(
    val fundType: ProgrammeFundType? = null,
    val fundAbbreviation: Set<InputTranslation>?,
    val fundingAmount: BigDecimal = BigDecimal.ZERO,
    val coFinancingRate: BigDecimal = BigDecimal.ZERO,

    val autoPublicContribution: BigDecimal = BigDecimal.ZERO,
    val otherPublicContribution: BigDecimal = BigDecimal.ZERO,
    val totalPublicContribution: BigDecimal = BigDecimal.ZERO,
    val privateContribution: BigDecimal = BigDecimal.ZERO,
    val totalContribution: BigDecimal = BigDecimal.ZERO,

    val totalFundAndContribution: BigDecimal = BigDecimal.ZERO
)
