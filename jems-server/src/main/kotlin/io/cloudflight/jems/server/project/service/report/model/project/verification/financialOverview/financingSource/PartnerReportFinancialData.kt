package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionOverview
import java.math.BigDecimal

data class PartnerReportFinancialData(
    val coFinancingFromAF: List<ProjectPartnerCoFinancing>,
    val contributionsFromAF: ProjectPartnerReportContributionOverview,
    val totalEligibleBudgetFromAF: BigDecimal,
    val flatRatesFromAF: ProjectPartnerBudgetOptions,
)
