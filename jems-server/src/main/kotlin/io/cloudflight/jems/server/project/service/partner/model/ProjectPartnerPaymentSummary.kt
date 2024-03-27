package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf

data class ProjectPartnerPaymentSummary(
    val partnerSummary: ProjectPartnerSummary,
    val partnerCoFinancing: MutableList<ProgrammeFund>,
    val partnerContributions: List<ProjectPartnerContribution> = emptyList(),
    val partnerContributionsSpf: List<ProjectPartnerContributionSpf> = emptyList()
)
