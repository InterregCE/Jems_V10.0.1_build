package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCostCalculator
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.*
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.partner.projectPartnerSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetProjectCoFinancingOverviewCalculatorServiceTest: UnitTest() {

    @MockK
    lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    @MockK
    lateinit var projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistenceProvider

    @MockK
    lateinit var getBudgetTotalCostCalculator: GetBudgetTotalCostCalculator

    @InjectMockKs
    lateinit var getProjectCoFinancingOverviewCalculatorService: GetProjectCoFinancingOverviewCalculatorService

    @Test
    fun getFullBudgetCoFinancing() {
        every { projectBudgetPersistence.getPartnersForProjectId(1, "v1.0") } returns
            listOf(projectPartnerSummary(id = 1L))
        every { projectPartnerCoFinancingPersistence.getAvailableFunds(any()) } returns
            setOf(
                ProgrammeFund(id = 1L, type = ProgrammeFundType.ERDF, selected = true)
            )

        every { getBudgetTotalCostCalculator.getBudgetTotalSpfCost(1L, "v1.0") } returns 100.toScaledBigDecimal()
        every { getBudgetTotalCostCalculator.getBudgetTotalCost(1L, "v1.0") } returns 200.toScaledBigDecimal()

        every {
            projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(any(), "v1.0")
        } returns ProjectPartnerCoFinancingAndContributionSpf(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ProgrammeFund(id = 1L, selected = true, type = ProgrammeFundType.ERDF),
                    percentage = BigDecimal(60)
                )
            ),
            partnerContributions = listOf(
                ProjectPartnerContributionSpf(amount = BigDecimal(30))
            )
        )

        every {
            projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(any(), "v1.0")
        } returns ProjectPartnerCoFinancingAndContribution(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ProgrammeFund(id = 1L, selected = true, type = ProgrammeFundType.ERDF),
                    percentage = BigDecimal(60)
                ),
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ProgrammeFund(id = 2L, selected = true, type = ProgrammeFundType.OTHER),
                    percentage = BigDecimal(30)
                )
            ),
            partnerContributions = listOf(
                ProjectPartnerContribution(isPartner = true, amount = BigDecimal(20))
            ),
            partnerAbbreviation = "test"
        )

        val overview = getProjectCoFinancingOverviewCalculatorService.getProjectCoFinancingOverview(1, "v1.0")
        Assertions.assertThat(overview.projectManagementCoFinancing.totalFundingAmount).isEqualTo(120.toScaledBigDecimal())
        Assertions.assertThat(overview.projectManagementCoFinancing.totalFundAndContribution).isEqualTo(200.toScaledBigDecimal())
        Assertions.assertThat(overview.projectManagementCoFinancing.totalEuFundAndContribution).isEqualTo(120.toScaledBigDecimal())
        Assertions.assertThat(overview.projectManagementCoFinancing.averageCoFinancingRate).isEqualTo(60.toScaledBigDecimal())
        Assertions.assertThat(overview.projectManagementCoFinancing.averageEuFinancingRate).isEqualTo(100.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.totalFundingAmount).isEqualTo(60.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.totalFundAndContribution).isEqualTo(100.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.totalEuFundAndContribution).isEqualTo(60.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.averageCoFinancingRate).isEqualTo(60.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.averageEuFinancingRate).isEqualTo(100.toScaledBigDecimal())
    }

}
