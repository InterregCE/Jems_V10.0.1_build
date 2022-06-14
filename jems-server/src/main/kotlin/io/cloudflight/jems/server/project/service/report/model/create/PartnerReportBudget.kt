package io.cloudflight.jems.server.project.service.report.model.create

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.contribution.create.CreateProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal

data class PartnerReportBudget(
    val contributions: List<CreateProjectPartnerReportContribution>,
    val lumpSums: List<PartnerReportLumpSum>,
    val unitCosts: Set<PartnerReportUnitCostBase>,
    val budgetPerPeriod: List<ProjectPartnerReportPeriod>,
    val spendingUpUntilNow: BigDecimal,
    val budgetOptions: ProjectPartnerBudgetOptions?,
)
