package io.cloudflight.jems.server.project.service.cofinancing.get_project_budget_cofinancing

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.cofinancing.get_project_cofinancing.GetProjectBudgetCoFinancing
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetProjectBudgetCoFinancingInteractorTest : UnitTest() {

    private val partner1Id = 1L
    private val partner2Id = 2L
    private val partner1 = ProjectPartnerSummary(
        id = partner1Id,
        abbreviation = "PP 2",
        role = ProjectPartnerRoleDTO.PARTNER,
        sortNumber = 2,
        country = "SK"
    )
    private val partner2 = ProjectPartnerSummary(
        id = partner2Id,
        abbreviation = "LP 1",
        role = ProjectPartnerRoleDTO.LEAD_PARTNER,
        sortNumber = 1
    )
    private val projectPartnerCoFinancingAndContribution = ProjectPartnerCoFinancingAndContribution(
        finances = emptyList(),
        partnerContributions = emptyList(),
        partnerAbbreviation = "test"
    )

    private val totalCostPartner1 = 80.toScaledBigDecimal()
    private val totalCostPartner2 = 15.toScaledBigDecimal()


    @MockK
    lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    @MockK
    lateinit var projectPartnerCoFinancingPersistenceProvider: ProjectPartnerCoFinancingPersistenceProvider

    @MockK
    lateinit var getBudgetTotalCost: GetBudgetTotalCost

    @InjectMockKs
    lateinit var getProjectBudgetCoFinancing: GetProjectBudgetCoFinancing

    @Test
    fun getBudgetCoFinancing() {
        every { projectBudgetPersistence.getPartnersForProjectId(1) } returns listOf(partner1, partner2)

        every { projectPartnerCoFinancingPersistenceProvider.getCoFinancingAndContributions(partner1Id, null) } returns projectPartnerCoFinancingAndContribution

        every { projectPartnerCoFinancingPersistenceProvider.getCoFinancingAndContributions(partner2Id, null) } returns projectPartnerCoFinancingAndContribution

        every { getBudgetTotalCost.getBudgetTotalCost(partner1Id) } returns totalCostPartner1
        every { getBudgetTotalCost.getBudgetTotalCost(partner2Id) } returns totalCostPartner2

        assertThat(getProjectBudgetCoFinancing.getBudgetCoFinancing(1))
            .containsExactlyInAnyOrder(
                PartnerBudgetCoFinancing(
                    partner = partner1,
                    projectPartnerCoFinancingAndContribution = projectPartnerCoFinancingAndContribution,
                    total = 80.toScaledBigDecimal()
                ),
                PartnerBudgetCoFinancing(
                    partner = partner2,
                    projectPartnerCoFinancingAndContribution = projectPartnerCoFinancingAndContribution,
                    total = 15.toScaledBigDecimal()
                )
            )
    }

}
