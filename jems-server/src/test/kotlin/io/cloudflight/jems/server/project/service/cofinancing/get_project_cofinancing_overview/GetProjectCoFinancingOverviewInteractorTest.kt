package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingByFundOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingCategoryOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.partner.projectPartnerSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetProjectCoFinancingOverviewInteractorTest : UnitTest() {

    companion object {
        private val projectManagementCoFinancing = ProjectCoFinancingCategoryOverview(
            fundOverviews = listOf(
                ProjectCoFinancingByFundOverview(20L, ProgrammeFundType.ERDF, fundAbbreviation = setOf()),
                ProjectCoFinancingByFundOverview(25L, ProgrammeFundType.OTHER, fundAbbreviation = setOf()),
            ),
            totalContribution = BigDecimal(800),
            totalFundingAmount = BigDecimal(200),
            totalFundAndContribution = BigDecimal(400),
            totalEuFundAndContribution = BigDecimal(400),
            averageCoFinancingRate = BigDecimal(80),
            averageEuFinancingRate = BigDecimal(100),
        )

        private val projectSpfCoFinancing = ProjectCoFinancingCategoryOverview(
            fundOverviews = listOf(
                ProjectCoFinancingByFundOverview(20L, ProgrammeFundType.ERDF, fundAbbreviation = setOf()),
            ),
            totalContribution = BigDecimal(190),
            totalFundingAmount = BigDecimal(200),
            totalFundAndContribution = BigDecimal(120),
            totalEuFundAndContribution = BigDecimal(70),
            averageCoFinancingRate = BigDecimal(50),
            averageEuFinancingRate = BigDecimal(100),
        )

        private val projectCoFinancingOverview = ProjectCoFinancingOverview(
            projectManagementCoFinancing = projectManagementCoFinancing,
            projectSpfCoFinancing = projectSpfCoFinancing
        )
    }

    @MockK
    lateinit var getProjectCoFinancingOverviewCalculatorService: GetProjectCoFinancingOverviewCalculatorService

    @InjectMockKs
    lateinit var getProjectCoFinancingOverview: GetProjectCoFinancingOverview

    @Test
    fun getProjectCoFinancingOverview() {
        every { getProjectCoFinancingOverviewCalculatorService.getProjectCoFinancingOverview(1, "v1.0") } returns
            projectCoFinancingOverview

        val overview = getProjectCoFinancingOverview.getProjectCoFinancingOverview(1, "v1.0")
        assertThat(overview.projectManagementCoFinancing.totalContribution).isEqualTo(BigDecimal(800))
        assertThat(overview.projectManagementCoFinancing.totalFundingAmount).isEqualTo(BigDecimal(200))
        assertThat(overview.projectManagementCoFinancing.totalFundAndContribution).isEqualTo(BigDecimal(400))
        assertThat(overview.projectManagementCoFinancing.totalEuFundAndContribution).isEqualTo(BigDecimal(400))
        assertThat(overview.projectManagementCoFinancing.averageCoFinancingRate).isEqualTo(BigDecimal(80))
        assertThat(overview.projectManagementCoFinancing.averageEuFinancingRate).isEqualTo(BigDecimal(100))

        assertThat(overview.projectSpfCoFinancing.totalContribution).isEqualTo(BigDecimal(190))
        assertThat(overview.projectSpfCoFinancing.totalFundingAmount).isEqualTo(BigDecimal(200))
        assertThat(overview.projectSpfCoFinancing.totalFundAndContribution).isEqualTo(BigDecimal(120))
        assertThat(overview.projectSpfCoFinancing.totalEuFundAndContribution).isEqualTo(BigDecimal(70))
        assertThat(overview.projectSpfCoFinancing.averageCoFinancingRate).isEqualTo(BigDecimal(50))
        assertThat(overview.projectSpfCoFinancing.averageEuFinancingRate).isEqualTo(BigDecimal(100))

    }

}
