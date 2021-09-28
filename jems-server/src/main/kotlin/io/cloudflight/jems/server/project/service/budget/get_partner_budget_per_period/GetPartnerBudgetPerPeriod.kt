package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period

import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.model.PartnerLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetCostsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.ProjectPartnerBudgetOptionsPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.model.BudgetPeriod
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class GetPartnerBudgetPerPeriod(
    private val persistence: ProjectBudgetPersistence,
    private val optionPersistence: ProjectPartnerBudgetOptionsPersistence,
    private val budgetCostsPersistence: ProjectPartnerBudgetCostsPersistence,
    private val getBudgetTotalCost: GetBudgetTotalCost,
    private val projectPersistence: ProjectPersistence,
    private val lumpSumPersistence: ProjectLumpSumPersistence,
) : GetPartnerBudgetPerPeriodInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    override fun getPartnerBudgetPerPeriod(projectId: Long, version: String?): List<ProjectPartnerBudgetPerPeriod> {
        val partners = persistence.getPartnersForProjectId(projectId = projectId, version).associateBy { it.id!! }
        val options = optionPersistence.getBudgetOptions(partners.keys, projectId, version).iterator().asSequence().associateBy { it.partnerId }
        val lumpSums = lumpSumPersistence.getLumpSums(projectId, version)

        return partners.map {
            getProjectPartnerBudgetPerPeriodForPartner(projectId, it.key, it.value, options[it.key], lumpSums, version)
        }
    }

    private fun getProjectPartnerBudgetPerPeriodForPartner(
        projectId: Long,
        partnerId: Long,
        partner: ProjectPartnerSummary,
        partnerBudgetOptions: ProjectPartnerBudgetOptions?,
        lumpSums: List<ProjectLumpSum>,
        version: String?
    ): ProjectPartnerBudgetPerPeriod {
        val staffCosts = budgetCostsPersistence.getBudgetStaffCosts(partnerId, version)
        val travelCosts = budgetCostsPersistence.getBudgetTravelAndAccommodationCosts(partnerId, version)
        val externalCosts = budgetCostsPersistence.getBudgetExternalExpertiseAndServicesCosts(partnerId, version)
        val equipmentCosts = budgetCostsPersistence.getBudgetEquipmentCosts(partnerId, version)
        val infrastructureCosts = budgetCostsPersistence.getBudgetInfrastructureAndWorksCosts(partnerId, version)
        val unitCosts = budgetCostsPersistence.getBudgetUnitCosts(partnerId, version)
        val projectPeriods = projectPersistence.getProjectPeriods(projectId)

        val lumpSumsForPartner = lumpSums.map { lumpSum ->
            PartnerLumpSum(
            period = lumpSum.period,
            amount = getAmountLumpSumPeriod(lumpSum.lumpSumContributions, partnerId)
        ) }

        // find last period that is not Preparation or Closure so that we can set a different total to it
        val lastPeriodNumber = if (projectPeriods.isNotEmpty()) { projectPeriods.maxOf { it.number } } else { 0 }
        val lastPeriod = projectPeriods.firstOrNull { it.number == lastPeriodNumber }

        // determine values for total budget of preparation and closure
        val preparationTotalBudget = lumpSumsForPartner.filter { it.period == 0 }.sumOf { it.amount }
        val closureTotalBudget = lumpSumsForPartner.filter { it.period == 255 }.sumOf { it.amount }

        // For each cost category of each partner, we filter out the right budget periods
        // we make a list of the amounts and we add them together
        val projectPartnerBudgetPerPeriod = ProjectPartnerBudgetPerPeriod(
            partner = partner,
            periodBudgets = projectPeriods.filter{ period -> period.number != lastPeriodNumber }.map { period ->
                getTotalBudgetForPeriod(
                    period,
                    partnerBudgetOptions,
                    staffCosts.map { staffCost -> getAmountOfCostPerPeriod(staffCost.budgetPeriods, period) }.sumOf { it },
                    travelCosts.map { travelCost -> getAmountOfCostPerPeriod(travelCost.budgetPeriods, period) }.sumOf { it },
                    externalCosts.map { externalCost -> getAmountOfCostPerPeriod(externalCost.budgetPeriods, period) }.sumOf { it },
                    equipmentCosts.map { equipmentCost -> getAmountOfCostPerPeriod(equipmentCost.budgetPeriods, period) }.sumOf { it },
                    infrastructureCosts.map { infrastructureCost -> getAmountOfCostPerPeriod(infrastructureCost.budgetPeriods, period) }.sumOf { it },
                    unitCosts.map { unitCost -> getAmountOfCostPerPeriod(unitCost.budgetPeriods, period) }.sumOf { it },
                    lumpSumsForPartner.filter {lumpSum -> lumpSum.period != null && lumpSum.period == period.number}.map { it.amount }.sumOf { it }
                )}.toMutableList(),
            totalPartnerBudget = getBudgetTotalCost.getBudgetTotalCost(partnerId, version)
        )

        // determine total budget without last period, preparation or closure
        val totalOfBudgetsPerPeriodBesidesLast = projectPartnerBudgetPerPeriod.periodBudgets.sumOf { it.totalBudgetPerPeriod }

        // add last Period with different total budget calculation
        if (lastPeriod != null) {
            projectPartnerBudgetPerPeriod.periodBudgets.add(
                ProjectPeriodBudget(
                    periodNumber = lastPeriodNumber,
                    periodStart = lastPeriod.start,
                    periodEnd = lastPeriod.end,
                    totalBudgetPerPeriod = projectPartnerBudgetPerPeriod.totalPartnerBudget
                        .minus(totalOfBudgetsPerPeriodBesidesLast)
                        .minus(preparationTotalBudget)
                        .minus(closureTotalBudget)
                        .setScale(2, RoundingMode.DOWN),
                    isLastPeriod = false
                )
            )
        }

        // add Preparation period which only has lump sum as total budget
        projectPartnerBudgetPerPeriod.periodBudgets.add(
            ProjectPeriodBudget(
                periodNumber = 0,
                periodStart = 0,
                periodEnd = 0,
                totalBudgetPerPeriod = preparationTotalBudget,
                isLastPeriod = false
        ))

        // add Closure period which only has lump sum as total budget
        projectPartnerBudgetPerPeriod.periodBudgets.add(
            ProjectPeriodBudget(
                periodNumber = 255,
                periodStart = 0,
                periodEnd = 0,
                totalBudgetPerPeriod = closureTotalBudget,
                isLastPeriod = true
            ))

        projectPartnerBudgetPerPeriod.periodBudgets.sortBy { it.periodNumber }

        return projectPartnerBudgetPerPeriod
    }

    private fun getTotalBudgetForPeriod(
        period: ProjectPeriod,
        options: ProjectPartnerBudgetOptions?,
        totalStaffCostsPerPeriod: BigDecimal,
        totalTravelCostsPerPeriod: BigDecimal,
        totalExternalCostsPerPeriod: BigDecimal,
        totalEquipmentCostsPerPeriod: BigDecimal,
        totalInfrastructureCostsPerPeriod: BigDecimal,
        totalUnitCostsPerPeriod: BigDecimal,
        totalLumpSum: BigDecimal,
    ): ProjectPeriodBudget {

        // For each flat rate, we check if it is set and then determine its value for the current period
        var staffCostFlatRateForPeriod = BigDecimal.ZERO
        var officeAndAdministrationOnDirectCostFlatRateForPeriod = BigDecimal.ZERO
        var officeAndAdministrationOnStaffCostFlatRateForPeriod = BigDecimal.ZERO
        var travelAndAccommodationOnStaffCostFlatRateForPeriod = BigDecimal.ZERO
        var otherCostsOnStaffCostFlatRateForPeriod = BigDecimal.ZERO

        if (options != null) {
            if (options.staffCostsFlatRate != null) {
                staffCostFlatRateForPeriod =
                    (options.staffCostsFlatRate.toBigDecimal()
                        .divide(BigDecimal(100))
                        .multiply(
                            totalTravelCostsPerPeriod
                                .add(totalExternalCostsPerPeriod)
                                .add(totalEquipmentCostsPerPeriod)
                                .add(totalInfrastructureCostsPerPeriod)
                        ))
                        .setScale(2, RoundingMode.DOWN)
            }

            if (options.officeAndAdministrationOnDirectCostsFlatRate != null) {
                officeAndAdministrationOnDirectCostFlatRateForPeriod =
                    (options.officeAndAdministrationOnDirectCostsFlatRate.toBigDecimal()
                        .divide(BigDecimal(100))
                        .multiply(
                            totalExternalCostsPerPeriod
                                .add(totalEquipmentCostsPerPeriod)
                                .add(totalInfrastructureCostsPerPeriod)
                                .add(totalTravelCostsPerPeriod)
                                .add(totalStaffCostsPerPeriod)
                        ))
                        .setScale(2, RoundingMode.DOWN)
            }

            if (options.officeAndAdministrationOnStaffCostsFlatRate != null) {
                officeAndAdministrationOnStaffCostFlatRateForPeriod =
                    (options.officeAndAdministrationOnStaffCostsFlatRate.toBigDecimal()
                        .divide(BigDecimal(100))
                        .multiply(totalStaffCostsPerPeriod
                            .add(staffCostFlatRateForPeriod)
                        ))
                        .setScale(2, RoundingMode.DOWN)
            }

            if (options.travelAndAccommodationOnStaffCostsFlatRate != null) {
                travelAndAccommodationOnStaffCostFlatRateForPeriod =
                    (options.travelAndAccommodationOnStaffCostsFlatRate.toBigDecimal()
                        .divide(BigDecimal(100))
                        .multiply(totalStaffCostsPerPeriod
                            .add(staffCostFlatRateForPeriod)
                        ))
                        .setScale(2, RoundingMode.DOWN)
            }
            if (options.otherCostsOnStaffCostsFlatRate != null) {
                otherCostsOnStaffCostFlatRateForPeriod =
                    (options.otherCostsOnStaffCostsFlatRate.toBigDecimal()
                        .divide(BigDecimal(100))
                        .multiply(totalStaffCostsPerPeriod))
                        .setScale(2, RoundingMode.DOWN)
            }
        }

        // We add together the costs, flat rates and lump sums
        // (if the cost is set, then there is no flat rate and the other way around) to get the total budget of a period
        val totalBudgetPerPeriod = totalStaffCostsPerPeriod
            .add(totalTravelCostsPerPeriod)
            .add(totalExternalCostsPerPeriod)
            .add(totalEquipmentCostsPerPeriod)
            .add(totalInfrastructureCostsPerPeriod)
            .add(totalUnitCostsPerPeriod)
            .add(staffCostFlatRateForPeriod)
            .add(officeAndAdministrationOnDirectCostFlatRateForPeriod)
            .add(officeAndAdministrationOnStaffCostFlatRateForPeriod)
            .add(travelAndAccommodationOnStaffCostFlatRateForPeriod)
            .add(otherCostsOnStaffCostFlatRateForPeriod)
            .add(totalLumpSum)

        return ProjectPeriodBudget(
            periodNumber = period.number,
            periodStart = period.start,
            periodEnd = period.end,
            totalBudgetPerPeriod = totalBudgetPerPeriod,
            isLastPeriod = false,
        );
    }

    private fun getAmountOfCostPerPeriod(costs: MutableSet<BudgetPeriod>, period: ProjectPeriod) =
        costs.filter { it.number == period.number }
            .map { it.amount }
            .fold(BigDecimal.ZERO) { first, second -> first.add(second) }

    private fun getAmountLumpSumPeriod(lumpSum: List<ProjectPartnerLumpSum>, partnerId: Long) =
        lumpSum.filter { it.partnerId == partnerId }
            .map { it.amount }
            .fold(BigDecimal.ZERO) { first, second -> first.add(second) }
}
