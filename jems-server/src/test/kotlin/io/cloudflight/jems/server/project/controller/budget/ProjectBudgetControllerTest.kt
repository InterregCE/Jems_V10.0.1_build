package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.project.dto.budget.ProjectBudgetOverviewPerPartnerPerPeriodDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetPerPeriodDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPeriodBudgetDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectUnitCostDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerCostTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundInteractor
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_period.GetPartnerBudgetPerPeriodInteractor
import io.cloudflight.jems.server.project.service.model.BudgetCostsDetail
import io.cloudflight.jems.server.project.service.model.ProjectBudgetOverviewPerPartnerPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerPeriod
import io.cloudflight.jems.server.project.service.model.ProjectPeriodBudget
import io.cloudflight.jems.server.project.service.model.ProjectPartnerCostType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.unitcost.get_project_unit_costs.GetProjectUnitCostsInteractor
import io.cloudflight.jems.server.project.service.unitcost.model.ProjectUnitCost
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ProjectBudgetControllerTest : UnitTest() {

    companion object {
        private val budgetPerPeriod = ProjectPartnerBudgetPerPeriod(
            partner = ProjectPartnerSummary(
                id = 2L,
                active = true,
                abbreviation = "partner",
                role = ProjectPartnerRole.LEAD_PARTNER,
                sortNumber = 3,
                country = "country",
                region = "region",
            ),
            periodBudgets = mutableListOf(
                ProjectPeriodBudget(
                    periodNumber = 1,
                    periodStart = 1,
                    periodEnd = 3,
                    totalBudgetPerPeriod = BigDecimal.ONE,
                    lastPeriod = true,
                    budgetPerPeriodDetail = BudgetCostsDetail()
                )
            ),
            totalPartnerBudget = BigDecimal.TEN,
            totalPartnerBudgetDetail = BudgetCostsDetail(),
            costType = ProjectPartnerCostType.Management
        )

        private val budgetOverviewPerPartnerPerPeriod =
            ProjectBudgetOverviewPerPartnerPerPeriod(
                partnersBudgetPerPeriod = listOf(budgetPerPeriod),
                totals = listOf(BigDecimal.TEN),
                totalsPercentage = listOf(BigDecimal.valueOf(100))
            )
        private val projectUnitCosts = ProjectUnitCost(
            costId = 1L,
            name = emptySet(),
            description = emptySet(),
            unitType = emptySet(),
            pricePerUnit = BigDecimal.TEN,
            numberOfUnits = BigDecimal.TEN,
            total = BigDecimal(100)
        )
    }

    @MockK
    lateinit var getPartnerBudgetPerPeriodInteractor: GetPartnerBudgetPerPeriodInteractor

    @MockK
    private lateinit var getProjectUnitCostsInteractor: GetProjectUnitCostsInteractor

    @MockK
    private lateinit var getPartnerBudgetPerFundInteractor: GetPartnerBudgetPerFundInteractor

    @InjectMockKs
    private lateinit var controller: ProjectBudgetController

    @Test
    fun getProjectPartnerBudgetPerPeriod() {
        val projectId = 1L
        every { getPartnerBudgetPerPeriodInteractor.getPartnerBudgetPerPeriod(projectId) } returns budgetOverviewPerPartnerPerPeriod

        assertThat(controller.getProjectPartnerBudgetPerPeriod(projectId)).isEqualTo(
            ProjectBudgetOverviewPerPartnerPerPeriodDTO(
                partnersBudgetPerPeriod = listOf(
                    ProjectPartnerBudgetPerPeriodDTO(
                        partner = ProjectPartnerSummaryDTO(
                            id = budgetPerPeriod.partner.id,
                            active = true,
                            abbreviation = budgetPerPeriod.partner.abbreviation,
                            role = ProjectPartnerRoleDTO.LEAD_PARTNER,
                            sortNumber = budgetPerPeriod.partner.sortNumber,
                            country = budgetPerPeriod.partner.country,
                            region = budgetPerPeriod.partner.region,
                        ),
                        periodBudgets = setOf(
                            ProjectPeriodBudgetDTO(
                                periodNumber = 1,
                                periodStart = 1,
                                periodEnd = 3,
                                totalBudgetPerPeriod = BigDecimal.ONE,
                                isLastPeriod = true
                            )
                        ),
                        totalPartnerBudget = budgetPerPeriod.totalPartnerBudget,
                        costType = ProjectPartnerCostTypeDTO.Management
                    )
                ),
                totals = listOf(BigDecimal.TEN),
                totalsPercentage = listOf(BigDecimal.valueOf(100)),
            )
        )
    }

    @Test
    fun getUnitCosts() {
        every { getProjectUnitCostsInteractor.getProjectUnitCost(1L) } returns listOf(projectUnitCosts)

        assertThat(controller.getProjectUnitCosts(1L)).isNotEmpty
        assertThat(controller.getProjectUnitCosts(1L)).containsExactly(
            ProjectUnitCostDTO(
                costId = 1L,
                name = emptySet(),
                description = emptySet(),
                unitType = emptySet(),
                pricePerUnit = BigDecimal.TEN,
                numberOfUnits = BigDecimal.TEN,
                total = BigDecimal(100)
            )
        )
    }

}
