package io.cloudflight.jems.server.project.service.budget.get_budget_funds_per_period

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.budget.model.ProjectSpfBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectFundsPerPeriod
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectFundBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.project_funds_per_period.ProjectPeriodFund
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.get_cofinancing.GetCoFinancingInteractor
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class GetBudgetFundsPerPeriod(
    private val projectPersistence: ProjectPersistence,
    private val callPersistence: CallPersistence,
    private val getPartnerBudgetPerPeriod: GetPartnerBudgetPerPeriodInteractor,
    private val getCoFinancing: GetCoFinancingInteractor,
    private val projectBudgetPersistence: ProjectBudgetPersistence,
    private val budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence
) : GetBudgetFundsPerPeriodInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetPartnerFundsPerPeriodException::class)
    override fun getBudgetFundsPerPeriod(projectId: Long, version: String?): ProjectFundsPerPeriod {
        val callDetail = callPersistence.getCallByProjectId(projectId)
        val callFunds = callDetail.funds.map { it.programmeFund }
        val projectPeriods = projectPersistence.getProjectPeriods(projectId, version)
        val spfBudgetFundsPerPeriod =
            if (callDetail.type == CallType.SPF)
                getSpfBudgetPerPeriodForFunds(callFunds, projectPeriods, projectId, version)
            else emptyList()
        return ProjectFundsPerPeriod(
            managementFundsPerPeriod = getBudgetPerPeriodForFunds(callFunds, projectPeriods, projectId, version),
            spfFundsPerPeriod = spfBudgetFundsPerPeriod
        )
    }

    private fun getBudgetPerPeriodForFunds(
        callFunds: List<ProgrammeFund>,
        projectPeriods: List<ProjectPeriod>,
        projectId: Long,
        version: String?
    ): List<ProjectFundBudgetPerPeriod> {
        val partnersBudgetPerPeriod =
            getPartnerBudgetPerPeriod.getPartnerBudgetPerPeriod(projectId, version).partnersBudgetPerPeriod
                .associateBy({ it.partner.id }, { it.periodBudgets })

        val partnersCoFinancing =
            getCoFinancing.getCoFinancingForPartnerList(partnersBudgetPerPeriod.keys.map { it!! }, projectId, version)
        return callFunds.map {
            getBudgetPerPeriodForFund(it, projectPeriods, partnersBudgetPerPeriod, partnersCoFinancing)
        }.toList()
    }

    private fun getSpfBudgetPerPeriodForFunds(
        callFunds: List<ProgrammeFund>,
        projectPeriods: List<ProjectPeriod>,
        projectId: Long,
        version: String?
    ): List<ProjectFundBudgetPerPeriod> {
        val spfBeneficiaries = projectBudgetPersistence.getPartnersForProjectId(projectId)

        val spfBudgetsPerPeriod = spfBeneficiaries.associateBy({it.id}, {
            if (it.id != null)
                projectBudgetPersistence.getSpfBudgetPerPeriod(it.id, projectId, version).toMutableList()
            else
                emptyList()
        })

        val spfPartnersCofinancing = spfBeneficiaries.associateBy({it.id}, {
            if (it.id != null)
                getCoFinancing.getSpfCoFinancing(it.id, version)
            else
                null
        })

        val spfTotalBudgets = spfBeneficiaries.associateBy({it.id}, {
            if (it.id != null)
                budgetCostsPersistence.getBudgetSpfCostTotal(it.id, version)
            else
                BigDecimal.ZERO
        })

        val budgetsPerPeriod = spfBeneficiaries.associateBy({it.id}, {
            if (it.id != null)
                callFunds.map { cost ->
                    getSpfBudgetPerPeriodForFund(
                        cost,
                        projectPeriods,
                        spfBudgetsPerPeriod[it.id]!!,
                        spfTotalBudgets[it.id]!!,
                        spfPartnersCofinancing[it.id]!!
                    )
                }.toList()
            else
                emptyList()
        })

        return budgetsPerPeriod.entries
            .map { it.value }
            .flatten()
            .groupBy {it.fund}
            .map {
                ProjectFundBudgetPerPeriod(
                    fund = it.value[0].fund,
                    costType = it.value[0].costType,
                    periodFunds = getTotalFundPerPeriodForPartners(it.value),
                    totalFundBudget = it.value.sumOf { value -> value.totalFundBudget }
        ) }
    }

    private fun getTotalFundPerPeriodForPartners(fundsPerPeriods: List<ProjectFundBudgetPerPeriod>): MutableList<ProjectPeriodFund> {
       return fundsPerPeriods
           .map {it.periodFunds}
           .flatten()
           .groupBy { it.periodNumber }
           .entries.map {
               ProjectPeriodFund(
                   periodNumber = it.key,
                   totalFundsPerPeriod = it.value.sumOf { period -> period.totalFundsPerPeriod }
               )
           }.toMutableList()
    }

    private fun getBudgetPerPeriodForFund(
        fund: ProgrammeFund,
        projectPeriods: List<ProjectPeriod>,
        partnersBudgetPerPeriod: Map<Long?, MutableList<ProjectPeriodBudget>>,
        partnersCoFinancing: Map<Long, List<ProjectPartnerCoFinancing>>?
    ): ProjectFundBudgetPerPeriod {
        val lastPeriodNumber = if (projectPeriods.isNotEmpty()) projectPeriods.maxOf { it.number } else 0
        val totalFundBudget = getTotalBudgetForFund(fund, partnersBudgetPerPeriod, partnersCoFinancing)

        val preparation = getTotalFundPerPeriodForPartners(0, fund, partnersBudgetPerPeriod, partnersCoFinancing)
        val closure = getTotalFundPerPeriodForPartners(255, fund, partnersBudgetPerPeriod, partnersCoFinancing)

        val periodFundsWithoutLastPeriod = projectPeriods.filter { period -> period.number != lastPeriodNumber }
            .map { getTotalFundPerPeriodForPartners(it.number, fund, partnersBudgetPerPeriod, partnersCoFinancing) }
            .toMutableList()
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

        return ProjectFundBudgetPerPeriod(
            fund = fund,
            costType = ProjectPartnerCostType.Management,
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
            totalFundsPerPeriod = partnersBudgetPerPeriod.entries.map {
                getTotalFundPerPeriodForPartner(
                    it.value.firstOrNull { budget -> budget.periodNumber == periodNumber },
                    partnersCoFinancing!![it.key], fund
                )
            }.sumOf { it }.setScale(2, RoundingMode.DOWN)
        )
    }

    private fun getTotalFundPerPeriodForPartner(
        partnerBudgetPerPeriod: ProjectPeriodBudget?,
        partnerCoFinancing: List<ProjectPartnerCoFinancing>?,
        fund: ProgrammeFund
    ): BigDecimal {
        if (partnerBudgetPerPeriod == null || partnerCoFinancing.isNullOrEmpty()) {
            return BigDecimal.ZERO
        }
        val selectedFund = partnerCoFinancing.firstOrNull { it.fund?.id == fund.id } ?: return BigDecimal.ZERO

        return partnerBudgetPerPeriod.totalBudgetPerPeriod.multiply(selectedFund.percentage)
            .divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN)
    }

    private fun getTotalBudgetForFund(
        fund: ProgrammeFund,
        partnersBudgetPerPeriod: Map<Long?, MutableList<ProjectPeriodBudget>>,
        partnersCoFinancing: Map<Long, List<ProjectPartnerCoFinancing>>?
    ): BigDecimal {
        return partnersBudgetPerPeriod.entries.map { periodBudgetList ->
            val periodBudgets = periodBudgetList.value.map { it.totalBudgetPerPeriod }
            val fundPercentage =
                partnersCoFinancing!![periodBudgetList.key]?.firstOrNull { it.fund?.id == fund.id }?.percentage
                    ?: BigDecimal.ZERO
            calculateTotalBudgetForFund(periodBudgets, fundPercentage)
        }.sumOf { it }.setScale(2, RoundingMode.DOWN)
    }

    private fun getSpfBudgetPerPeriodForFund(
        fund: ProgrammeFund,
        projectPeriods: List<ProjectPeriod>,
        spfBudgetPerPeriod: List<ProjectSpfBudgetPerPeriod>,
        spfTotalBudget: BigDecimal,
        spfCoFinancing: ProjectPartnerCoFinancingAndContributionSpf
    ): ProjectFundBudgetPerPeriod {
        val totalFundBudget = getTotalSpfBudgetForFundFund(fund, spfTotalBudget, spfCoFinancing)
        val lastPeriodNumber = if (projectPeriods.isNotEmpty()) projectPeriods.maxOf { it.number } else 0
        val preparation = ProjectPeriodFund(0, BigDecimal.ZERO)
        val closure = ProjectPeriodFund(255, BigDecimal.ZERO)
        val periodFundsWithoutLastPeriod = projectPeriods.filter { period -> period.number != lastPeriodNumber }
            .map { getSpfTotalFundPerPeriod(it.number, fund, spfBudgetPerPeriod, spfCoFinancing) }
            .toMutableList()
        val lastPeriodFundBudget = totalFundBudget
            .minus(periodFundsWithoutLastPeriod.sumOf { it.totalFundsPerPeriod })
            .setScale(2, RoundingMode.DOWN)
        val lastPeriod = ProjectPeriodFund(periodNumber = lastPeriodNumber, totalFundsPerPeriod = lastPeriodFundBudget)
        if (projectPeriods.isNotEmpty()) {
            periodFundsWithoutLastPeriod.add(lastPeriod)
        }
        periodFundsWithoutLastPeriod.add(preparation)
        periodFundsWithoutLastPeriod.add(closure)
        return ProjectFundBudgetPerPeriod(
            fund = fund,
            costType = ProjectPartnerCostType.Spf,
            periodFunds = periodFundsWithoutLastPeriod.sortedBy { it.periodNumber }.toMutableList(),
            totalFundBudget = totalFundBudget
        )
    }

    private fun getSpfTotalFundPerPeriod(
        periodNumber: Int,
        fund: ProgrammeFund,
        budgetsPerPeriod: List<ProjectSpfBudgetPerPeriod>,
        spfBeneficiaryCoFinancing: ProjectPartnerCoFinancingAndContributionSpf
    ): ProjectPeriodFund {
        val selectedFund = spfBeneficiaryCoFinancing.finances.firstOrNull { it.fund?.id == fund.id }
        var totalFundPerPeriod = BigDecimal.ZERO
        if (selectedFund != null) {
            val totalPerPeriod =
                budgetsPerPeriod.firstOrNull { it.periodNumber == periodNumber }?.spfCostPerPeriod ?: BigDecimal.ZERO
            totalFundPerPeriod = totalPerPeriod
                .multiply(selectedFund.percentage)
                .divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN)
        }
        return ProjectPeriodFund(
            periodNumber = periodNumber,
            totalFundsPerPeriod = totalFundPerPeriod
        )
    }

    private fun getTotalSpfBudgetForFundFund(
        fund: ProgrammeFund,
        totalSpfCosts: BigDecimal,
        spfBeneficiaryCoFinancing: ProjectPartnerCoFinancingAndContributionSpf
    ): BigDecimal {
        val fundPercentage =
            spfBeneficiaryCoFinancing.finances.firstOrNull { it.fund?.id == fund.id }?.percentage ?: BigDecimal.ZERO
        return calculateTotalBudgetForFund(listOf(totalSpfCosts), fundPercentage)
            .setScale(2, RoundingMode.DOWN)
    }

    private fun calculateTotalBudgetForFund(
        budgetsPerPeriod: List<BigDecimal>,
        fundPercentage: BigDecimal
    ): BigDecimal =
        budgetsPerPeriod.sumOf { it }
            .multiply(fundPercentage)
            .divide(BigDecimal.valueOf(100))
}
