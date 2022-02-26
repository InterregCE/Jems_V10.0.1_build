package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.project.service.budget.model.ProjectPartnerBudget
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.PartnerTotalBudgetPerCostCategory


interface PartnerBudgetPerPeriodCalculatorService {

    fun calculate(
        partners: List<ProjectPartnerSummary>,
        budgetOptions: List<ProjectPartnerBudgetOptions>,
        budgetPerPartner: List<ProjectPartnerBudget>,
        lumpSums: List<ProjectLumpSum>,
        projectPeriods: List<ProjectPeriod>,
        partnersTotalBudgetPerCostCategory: Map<Long, PartnerTotalBudgetPerCostCategory>
    ): ProjectBudgetOverviewPerPartnerPerPeriod
}
