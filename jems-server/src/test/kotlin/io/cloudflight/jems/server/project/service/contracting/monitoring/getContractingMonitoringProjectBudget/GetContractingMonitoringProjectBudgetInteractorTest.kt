package io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringProjectBudget

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview.GetProjectCoFinancingOverviewCalculatorService
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingByFundOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingCategoryOverview
import io.cloudflight.jems.server.project.service.cofinancing.model.ProjectCoFinancingOverview
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetContractingMonitoringProjectBudgetInteractorTest: UnitTest() {

    companion object {
        private const val projectId = 10L
        private const val version = "v2.0"

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
    lateinit var interactor: GetContractingMonitoringProjectBudget

    @Test
    fun `get project budget`() {
        every { getProjectCoFinancingOverviewCalculatorService.getProjectCoFinancingOverview(projectId, version) } returns
            projectCoFinancingOverview

        assertThat(interactor.getProjectBudget(projectId, version)).isEqualTo(BigDecimal(520))
    }
}
