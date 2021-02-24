package io.cloudflight.jems.api.project.dto.partner.cofinancing

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO

data class ProjectPartnerCoFinancingOutputDTO(
    val fundType: ProjectPartnerCoFinancingFundType,
    val percentage: Int,
    val fund: ProgrammeFundDTO?
)
