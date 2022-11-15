package io.cloudflight.jems.server.project.service.report.model.partner.base.create

import io.cloudflight.jems.server.project.service.report.model.partner.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod

data class PartnerReportBudget(
    val contributions: List<CreateProjectPartnerReportContribution>,
    val availableLumpSums: List<PartnerReportLumpSum>,
    val unitCosts: Set<PartnerReportUnitCostBase>,
    val investments: List<PartnerReportInvestment>,
    val budgetPerPeriod: List<ProjectPartnerReportPeriod>,
    val expenditureSetup: ReportExpenditureCostCategory,
    val previouslyReportedCoFinancing: PreviouslyReportedCoFinancing,
)
