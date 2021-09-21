package io.cloudflight.jems.api.project.dto.partner.cofinancing

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.math.BigDecimal

data class ProjectPartnerCoFinancingOutputDTO(
    val fundType: ProjectPartnerCoFinancingFundTypeDTO,
    val percentage: BigDecimal,
    val fund: ProgrammeFundDTO?
)
