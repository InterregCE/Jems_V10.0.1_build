package io.cloudflight.jems.server.project.service.report.model.contribution.create

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import java.math.BigDecimal
import java.util.UUID

data class CreateProjectPartnerReportContribution(
    val sourceOfContribution: String?,
    val legalStatus: ProjectPartnerContributionStatus?,
    val idFromApplicationForm: Long?,
    val historyIdentifier: UUID,
    val createdInThisReport: Boolean,
    val amount: BigDecimal,
    val previouslyReported: BigDecimal,
    val currentlyReported: BigDecimal,
)
