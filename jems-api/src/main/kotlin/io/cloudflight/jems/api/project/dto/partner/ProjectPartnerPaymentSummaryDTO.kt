package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionDTO

data class  ProjectPartnerPaymentSummaryDTO (
    val partnerSummary: ProjectPartnerSummaryDTO,
    val partnerCoFinancing: List<ProgrammeFundDTO>,
    val partnerContributions: List<ProjectPartnerContributionDTO> = emptyList(),
    val partnerContributionsSpf: List<ProjectPartnerContributionDTO> = emptyList()
)
