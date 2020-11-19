package io.cloudflight.jems.api.project.dto.partner.cofinancing

import io.cloudflight.jems.api.programme.dto.ProgrammeFundOutputDTO

data class ProjectPartnerCoFinancingOutputDTO(
    val id: Long,
    val percentage: Int,
    val fund: ProgrammeFundOutputDTO?
)
