package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.project.service.budget.model.PartnersAggregatedInfo
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.math.BigDecimal

interface PartnerBudgetPerPeriodCalculatorService {

    @Suppress("LongParameterList")
    fun calculate(
        partnersInfo : PartnersAggregatedInfo,
        lumpSums: List<ProjectLumpSum>,
        projectPeriods: List<ProjectPeriod>,
        spfBudgetPerPeriod: List<ProjectSpfBudgetPerPeriod>,
        spfTotalBudget: BigDecimal,
        spfBeneficiary: ProjectPartnerSummary?
    ): ProjectBudgetOverviewPerPartnerPerPeriod
}
