package io.cloudflight.jems.server.project.service.report.model.contribution.update

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal

data class UpdateProjectPartnerReportContributionCustom(
    val sourceOfContribution: String,
    val legalStatus: ProjectPartnerContributionStatus,
    val currentlyReported: BigDecimal,
)
