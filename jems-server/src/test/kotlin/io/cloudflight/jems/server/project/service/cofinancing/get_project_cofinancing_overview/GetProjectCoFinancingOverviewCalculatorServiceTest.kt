package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingByFundOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingCategoryOverview
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCostCalculator
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.*
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.partner.projectPartnerSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetProjectCoFinancingOverviewCalculatorServiceTest: UnitTest() {

    private val expectedManagement = ProjectCoFinancingCategoryOverview(
        fundOverviews = listOf(
            ProjectCoFinancingByFundOverview(
                fundId = 1L,
                fundType = ProgrammeFundType.ERDF,
                fundAbbreviation = emptySet(),
                fundingAmount = BigDecimal.valueOf(120_00L, 2),
                coFinancingRate = BigDecimal.valueOf(75_00L, 2),
                autoPublicContribution = BigDecimal.valueOf(40L),
                otherPublicContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal.valueOf(40L),
                privateContribution = BigDecimal.ZERO,
                totalContribution = BigDecimal.valueOf(40L),
                totalFundAndContribution = BigDecimal.valueOf(160_00L, 2),
            )
        ),
        totalFundingAmount = BigDecimal.valueOf(120_00L, 2),
        totalEuFundingAmount = BigDecimal.valueOf(120_00L, 2),
        averageCoFinancingRate = BigDecimal.valueOf(60_00L, 2),
        averageEuFinancingRate = BigDecimal.valueOf(75_00L, 2),

        totalAutoPublicContribution = BigDecimal.valueOf(40L),
        totalEuAutoPublicContribution = BigDecimal.valueOf(40L),
        totalOtherPublicContribution = BigDecimal.ZERO,
        totalEuOtherPublicContribution = BigDecimal.ZERO,
        totalPublicContribution = BigDecimal.valueOf(40L),
        totalEuPublicContribution = BigDecimal.valueOf(40L),
        totalPrivateContribution = BigDecimal.ZERO,
        totalEuPrivateContribution = BigDecimal.ZERO,
        totalContribution = BigDecimal.valueOf(40L),
        totalEuContribution = BigDecimal.valueOf(40L),

        totalFundAndContribution = BigDecimal.valueOf(200_00L, 2),
        totalEuFundAndContribution = BigDecimal.valueOf(160_00L, 2),
    )
    private val expectedSpf = ProjectCoFinancingCategoryOverview(
        fundOverviews = listOf(
            ProjectCoFinancingByFundOverview(
                fundId = 1L,
                fundType = ProgrammeFundType.ERDF,
                fundAbbreviation = emptySet(),
                fundingAmount = BigDecimal.valueOf(60_00L, 2),
                coFinancingRate = BigDecimal.valueOf(100_00L, 2),
                autoPublicContribution = BigDecimal.ZERO,
                otherPublicContribution = BigDecimal.ZERO,
                totalPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                totalContribution = BigDecimal.ZERO,
                totalFundAndContribution = BigDecimal.valueOf(60_00L, 2),
            )
        ),
        totalFundingAmount = BigDecimal.valueOf(60_00L, 2),
        totalEuFundingAmount = BigDecimal.valueOf(60_00L, 2),
        averageCoFinancingRate = BigDecimal.valueOf(60_00L, 2),
        averageEuFinancingRate = BigDecimal.valueOf(100_00L, 2),

        totalAutoPublicContribution = BigDecimal.ZERO,
        totalEuAutoPublicContribution = BigDecimal.ZERO,
        totalOtherPublicContribution = BigDecimal.ZERO,
        totalEuOtherPublicContribution = BigDecimal.ZERO,
        totalPublicContribution = BigDecimal.ZERO,
        totalEuPublicContribution = BigDecimal.ZERO,
        totalPrivateContribution = BigDecimal.ZERO,
        totalEuPrivateContribution = BigDecimal.ZERO,
        totalContribution = BigDecimal.ZERO,
        totalEuContribution = BigDecimal.ZERO,

        totalFundAndContribution = BigDecimal.valueOf(100_00L, 2),
        totalEuFundAndContribution = BigDecimal.valueOf(60_00L, 2),
    )

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
        every { getBudgetTotalCostCalculator.getBudgetTotalManagementCost(1L, "v1.0") } returns 200.toScaledBigDecimal()

        every {
            projectPartnerCoFinancingPersistence.getSpfCoFinancingAndContributions(any(), "v1.0")
        } returns ProjectPartnerCoFinancingAndContributionSpf(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ProgrammeFund(id = 1L, selected = true, type = ProgrammeFundType.ERDF),
                    percentage = BigDecimal(60)
                ),
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                    percentage = BigDecimal(40)
                ),
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
                ),
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                    percentage = BigDecimal.valueOf(10),
                ),
            ),
            partnerContributions = listOf(
                ProjectPartnerContribution(isPartner = true, amount = BigDecimal(20)),
                ProjectPartnerContribution(isPartner = false, status = ProjectPartnerContributionStatus.AutomaticPublic, amount = BigDecimal(40)),
            ),
            partnerAbbreviation = "test"
        )

        val overview = getProjectCoFinancingOverviewCalculatorService.getProjectCoFinancingOverview(1, "v1.0")
        Assertions.assertThat(overview.projectManagementCoFinancing.totalFundingAmount).isEqualTo(120.toScaledBigDecimal())
        Assertions.assertThat(overview.projectManagementCoFinancing.totalFundAndContribution).isEqualTo(200.toScaledBigDecimal())
        Assertions.assertThat(overview.projectManagementCoFinancing.totalEuFundAndContribution).isEqualTo(160.toScaledBigDecimal())
        Assertions.assertThat(overview.projectManagementCoFinancing.averageCoFinancingRate).isEqualTo(60.toScaledBigDecimal())
        Assertions.assertThat(overview.projectManagementCoFinancing.averageEuFinancingRate).isEqualTo(75.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.totalFundingAmount).isEqualTo(60.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.totalFundAndContribution).isEqualTo(100.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.totalEuFundAndContribution).isEqualTo(60.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.averageCoFinancingRate).isEqualTo(60.toScaledBigDecimal())
        Assertions.assertThat(overview.projectSpfCoFinancing.averageEuFinancingRate).isEqualTo(100.toScaledBigDecimal())
        assertThat(overview.projectManagementCoFinancing).isEqualTo(expectedManagement)
        assertThat(overview.projectSpfCoFinancing).isEqualTo(expectedSpf)
    }

}
