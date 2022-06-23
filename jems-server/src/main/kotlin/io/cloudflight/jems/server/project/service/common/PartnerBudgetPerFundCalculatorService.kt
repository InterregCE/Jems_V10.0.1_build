package io.cloudflight.jems.server.project.service.common

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetSpfCoFinancing
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary

interface PartnerBudgetPerFundCalculatorService {
    fun calculate(
        partners: List<ProjectPartnerSummary>,
        projectFunds: List<ProgrammeFund>,
        coFinancing: List<PartnerBudgetCoFinancing>,
        spfCoFinancing: List<PartnerBudgetSpfCoFinancing?>
    ): List<ProjectPartnerBudgetPerFund>
}
