package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.project.service.budget.model.PartnersAggregatedInfo
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.math.BigDecimal

interface PartnerBudgetPerPeriodCalculatorService {

    fun calculate(
        partnersInfo : PartnersAggregatedInfo,
        lumpSums: List<ProjectLumpSum>,
        projectPeriods: List<ProjectPeriod>,
        spfPartnerBudgetPerPeriod: List<ProjectPartnerBudgetPerPeriod>
    ): ProjectBudgetOverviewPerPartnerPerPeriod

    fun calculateSpfPartnerBudgetPerPeriod(
        spfBeneficiary: ProjectPartnerSummary,
        spfBudgetPerPeriod: List<ProjectSpfBudgetPerPeriod>,
        spfTotalBudget: BigDecimal,
        projectPeriods: List<ProjectPeriod>,
    ): List<ProjectPartnerBudgetPerPeriod>

}
