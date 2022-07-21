package io.cloudflight.jems.server.project.service.report.model.create

import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal

data class PartnerReportBudget(
    val contributions: List<CreateProjectPartnerReportContribution>,
    val lumpSums: List<PartnerReportLumpSum>,
    val unitCosts: Set<PartnerReportUnitCostBase>,
    val budgetPerPeriod: List<ProjectPartnerReportPeriod>,
    val expenditureSetup: ReportExpenditureCostCategory,
    val previouslyReportedCoFinancing: PreviouslyReportedCoFinancing,
)
