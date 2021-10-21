package io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing_overview

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.partner.projectPartnerSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetProjectCoFinancingOverviewInteractorTest : UnitTest() {

    @MockK
    lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    @MockK
    lateinit var projectPartnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistenceProvider

    @MockK
    lateinit var getBudgetTotalCost: GetBudgetTotalCost

    @InjectMockKs
    lateinit var getProjectCoFinancingOverview: GetProjectCoFinancingOverview

    @Test
    fun getBudgetCoFinancing() {
        every { projectBudgetPersistence.getPartnersForProjectId(1) } returns
            listOf(projectPartnerSummary(id = 1L), projectPartnerSummary(id = 2L))
        every { projectPartnerCoFinancingPersistence.getAvailableFunds(any()) } returns
            setOf(
                ProgrammeFund(id = 1L, type = ProgrammeFundType.ERDF, selected = true),
                ProgrammeFund(id = 2L, type = ProgrammeFundType.OTHER, selected = true)
            )

        every { getBudgetTotalCost.getBudgetTotalCost(1L) } returns 100.toScaledBigDecimal()
        every { getBudgetTotalCost.getBudgetTotalCost(2L) } returns 100.toScaledBigDecimal()

        every {
            projectPartnerCoFinancingPersistence.getCoFinancingAndContributions(any(), null)
        } returns ProjectPartnerCoFinancingAndContribution(
            finances = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ProgrammeFund(id = 1L, selected = true, type = ProgrammeFundType.ERDF),
                    percentage = BigDecimal(50)
                ),
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ProgrammeFund(id = 2L, selected = true, type = ProgrammeFundType.OTHER),
                    percentage = BigDecimal(40)
                )
            ),
            partnerContributions = listOf(
                ProjectPartnerContribution(isPartner = true, amount = BigDecimal(20))
            ),
            partnerAbbreviation = "test"
        )

        val overview = getProjectCoFinancingOverview.getProjectCoFinancingOverview(1, null)
        assertThat(overview.totalFundingAmount).isEqualTo(180.toScaledBigDecimal())
        assertThat(overview.totalFundAndContribution).isEqualTo(200.toScaledBigDecimal())
        assertThat(overview.totalEuFundAndContribution).isEqualTo(100.toScaledBigDecimal())
        assertThat(overview.averageCoFinancingRate).isEqualTo(90.toScaledBigDecimal())
        assertThat(overview.averageEuFinancingRate).isEqualTo(100.toScaledBigDecimal())
    }
}
