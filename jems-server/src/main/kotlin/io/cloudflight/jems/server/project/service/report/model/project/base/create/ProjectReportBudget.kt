package io.cloudflight.jems.server.project.service.report.model.project.base.create

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaimCreate

data class ProjectReportBudget(
    val coFinancing: PreviouslyProjectReportedCoFinancing,
    val costCategorySetup: ReportCertificateCostCategory,
    val availableLumpSums: List<ProjectReportLumpSum>,
    val unitCosts: Set<ProjectReportUnitCostBase>,
    val investments: List<ProjectReportInvestment>,
    val spfContributionClaims: List<ProjectReportSpfContributionClaimCreate>
)
