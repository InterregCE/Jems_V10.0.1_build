package io.cloudflight.jems.server.project.service.budget.get_partner_funds_per_period

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectPartnerFundsPerPeriod
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectPeriodFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing.GetCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing.GetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class GetPartnerFundsPerPeriod(
    private val projectPersistence: ProjectPersistence,
    private val callPersistence: CallPersistence,
    private val getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriodInteractor,
    private val getCoFinancing: GetCoFinancingInteractor
) : GetPartnerFundsPerPeriodInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetPartnerFundsPerPeriodException::class)
    override fun getPartnerFundsPerPeriod(projectId: Long, version: String?): List<ProjectPartnerFundsPerPeriod> {
        val callFunds = callPersistence.getCallByProjectId(projectId).funds.map { it.programmeFund }
        val projectPeriods = projectPersistence.getProjectPeriods(projectId, version)
        val partnersBudgetPerPeriod =
            getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId, version)
            .associateBy({ it.partner.id }, { it.periodBudgets })

        val partnersCoFinancing = getCoFinancing.getCoFinancingForPartnerList(partnersBudgetPerPeriod.keys.map { it!! }, projectId, version)

        return callFunds.map { getBudgetPerPeriodForFund(it, projectPeriods, partnersBudgetPerPeriod, partnersCoFinancing) }.toList()
    }

    private fun getBudgetPerPeriodForFund(
        fund: ProgrammeFund,
        projectPeriods: List<ProjectPeriod>,
        partnersBudgetPerPeriod: Map<Long?, MutableList<ProjectPeriodBudget>>,
        partnersCoFinancing: Map<Long, List<ProjectPartnerCoFinancing>>?
        ): ProjectPartnerFundsPerPeriod {

        val lastPeriodNumber = if (projectPeriods.isNotEmpty()) { projectPeriods.maxOf { it.number } } else { 0 }
        val totalFundBudget = getTotalBudgetForFund(fund, partnersBudgetPerPeriod, partnersCoFinancing)

        val preparation = getTotalFundPerPeriodForPartners(0, fund, partnersBudgetPerPeriod, partnersCoFinancing)
        val closure = getTotalFundPerPeriodForPartners(255, fund, partnersBudgetPerPeriod, partnersCoFinancing)

        val periodFundsWithoutLastPeriod = projectPeriods.filter{ period -> period.number != lastPeriodNumber }.map { getTotalFundPerPeriodForPartners(it.number, fund, partnersBudgetPerPeriod, partnersCoFinancing) }.toMutableList()
        val lastPeriodFundBudget = totalFundBudget
            .minus(periodFundsWithoutLastPeriod.sumOf { it.totalFundsPerPeriod })
            .minus(preparation.totalFundsPerPeriod)
            .minus(closure.totalFundsPerPeriod)
            .setScale(2, RoundingMode.DOWN)
        val lastPeriod = ProjectPeriodFund(periodNumber = lastPeriodNumber, totalFundsPerPeriod = lastPeriodFundBudget)

        if (projectPeriods.isNotEmpty()) {
            periodFundsWithoutLastPeriod.add(lastPeriod)
        }

        periodFundsWithoutLastPeriod.add(preparation)
        periodFundsWithoutLastPeriod.add(closure)

        return ProjectPartnerFundsPerPeriod(
            fund = fund,
            periodFunds = periodFundsWithoutLastPeriod.sortedBy { it.periodNumber }.toMutableList(),
            totalFundBudget = totalFundBudget
        )
    }

    private fun getTotalFundPerPeriodForPartners(
        periodNumber: Int,
        fund: ProgrammeFund,
        partnersBudgetPerPeriod: Map<Long?, MutableList<ProjectPeriodBudget>>,
        partnersCoFinancing: Map<Long, List<ProjectPartnerCoFinancing>>?
    ): ProjectPeriodFund {
        return ProjectPeriodFund(
            periodNumber = periodNumber,
            totalFundsPerPeriod = partnersBudgetPerPeriod.entries.map { getTotalFundPerPeriodForPartner(
                it.value.firstOrNull { budget -> budget.periodNumber == periodNumber },
                partnersCoFinancing!![it.key], fund
            ) }.sumOf { it }.setScale(2, RoundingMode.DOWN)
        )
    }

    private fun getTotalFundPerPeriodForPartner(
        partnerBudgetPerPeriod: ProjectPeriodBudget?,
        partnerCoFinancing: List<ProjectPartnerCoFinancing>?,
        fund: ProgrammeFund
    ) : BigDecimal {
        if (partnerBudgetPerPeriod == null || partnerCoFinancing.isNullOrEmpty()) {
            return BigDecimal.ZERO
        }
        val selectedFund = partnerCoFinancing.firstOrNull { it.fund?.id == fund.id} ?: return BigDecimal.ZERO

        return partnerBudgetPerPeriod.totalBudgetPerPeriod.multiply(selectedFund.percentage).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN)
    }

    private fun getTotalBudgetForFund(
        fund: ProgrammeFund,
        partnersBudgetPerPeriod: Map<Long?, MutableList<ProjectPeriodBudget>>,
        partnersCoFinancing: Map<Long, List<ProjectPartnerCoFinancing>>?) : BigDecimal {

        return partnersBudgetPerPeriod.entries.map { periodBudgetList ->
            periodBudgetList.value.sumOf { it.totalBudgetPerPeriod }
                .multiply(partnersCoFinancing!![periodBudgetList.key]?.firstOrNull { it.fund?.id == fund.id}?.percentage ?: BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(100))
        }.sumOf { it }.setScale(2, RoundingMode.DOWN)
    }
}
