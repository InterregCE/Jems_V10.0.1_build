package io.cloudflight.jems.server.project.service.report.model.contribution.update

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal

data class UpdateProjectPartnerReportContributionExisting(
    val id: Long,
    val currentlyReported: BigDecimal,
    val sourceOfContribution: String? = null,
    val legalStatus: ProjectPartnerContributionStatus? = null,
)
