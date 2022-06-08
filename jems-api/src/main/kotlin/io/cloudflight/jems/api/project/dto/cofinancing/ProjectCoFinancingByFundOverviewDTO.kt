package io.cloudflight.jems.api.project.dto.cofinancing

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import java.math.BigDecimal

data class ProjectCoFinancingByFundOverviewDTO(
    val fundId : Long,
    val fundType: ProgrammeFundTypeDTO? = null,
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
