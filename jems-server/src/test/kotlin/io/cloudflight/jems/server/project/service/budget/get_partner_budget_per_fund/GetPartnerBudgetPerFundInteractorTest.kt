package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_fund

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.common.PartnerBudgetPerFundCalculatorService
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class GetPartnerBudgetPerFundInteractorTest : UnitTest() {
    private val partner1Id = 1L
    private val partner2Id = 2L
    private val partner1 = ProjectPartnerSummary(
        id = partner1Id,
        active = true,
        abbreviation = "PP 2",
        role = ProjectPartnerRole.PARTNER,
        sortNumber = 2,
        country = "SK"
    )
    private val partner2 = ProjectPartnerSummary(
        id = partner2Id,
        active = true,
        abbreviation = "LP 1",
        role = ProjectPartnerRole.LEAD_PARTNER,
        sortNumber = 1
    )
    private val projectPartnerCoFinancingAndContribution = ProjectPartnerCoFinancingAndContribution(
        finances = emptyList(),
        partnerContributions = emptyList(),
        partnerAbbreviation = ""
    )
    private val call = CallDetail(
        id = 1,
        name = "call",
        status = CallStatus.PUBLISHED,
        startDate = ZonedDateTime.now(),
        endDateStep1 = ZonedDateTime.now(),
        endDate = ZonedDateTime.now(),
        isAdditionalFundAllowed = true,
        lengthOfPeriod = null,
        applicationFormFieldConfigurations = mutableSetOf(),
        preSubmissionCheckPluginKey = null
    )

    private val totalCostPartner1 = 80.toScaledBigDecimal()
    private val totalCostPartner2 = 15.toScaledBigDecimal()

    @MockK
    lateinit var callPersistence: CallPersistence

    @MockK
    lateinit var projectBudgetPersistence: ProjectBudgetPersistence

    @MockK
    lateinit var projectPartnerCoFinancingPersistenceProvider: ProjectPartnerCoFinancingPersistenceProvider

    @MockK
    lateinit var getBudgetTotalCost: GetBudgetTotalCost

    @MockK
    lateinit var partnerBudgetPerFundCalculator: PartnerBudgetPerFundCalculatorService

    @InjectMockKs
    lateinit var getPartnerBudgetPerFund: GetPartnerBudgetPerFund

    @Test
    fun getBudgetCoFinancing() {

        val result1 = ProjectPartnerBudgetPerFund(
            partner1,
            budgetPerFund = emptySet(),
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            totalPartnerContribution = BigDecimal.ZERO,
            totalEligibleBudget = BigDecimal.ZERO,
            percentageOfTotalEligibleBudget = BigDecimal.ZERO
        )

        val result2 = ProjectPartnerBudgetPerFund(
            partner2,
            budgetPerFund = emptySet(),
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            totalPartnerContribution = BigDecimal.ZERO,
            totalEligibleBudget = BigDecimal.ZERO,
            percentageOfTotalEligibleBudget = BigDecimal.ZERO
        )

        val cof1 = PartnerBudgetCoFinancing(
            partner = partner1,
            projectPartnerCoFinancingAndContribution =
            ProjectPartnerCoFinancingAndContribution(
                finances = emptyList(),
                partnerContributions = emptyList(),
                partnerAbbreviation = ""
            ),
            total = totalCostPartner1
        )

        val cof2 = PartnerBudgetCoFinancing(
            partner = partner2,
            projectPartnerCoFinancingAndContribution =
            ProjectPartnerCoFinancingAndContribution(
                finances = emptyList(),
                partnerContributions = emptyList(),
                partnerAbbreviation = ""
            ),
            total = totalCostPartner2
        )

        every { projectBudgetPersistence.getPartnersForProjectId(1) } returns listOf(partner1, partner2)

        every { callPersistence.getCallByProjectId(1) } returns call

        every { projectPartnerCoFinancingPersistenceProvider.getCoFinancingAndContributions(partner1Id, null) } returns projectPartnerCoFinancingAndContribution

        every { projectPartnerCoFinancingPersistenceProvider.getCoFinancingAndContributions(partner2Id, null) } returns projectPartnerCoFinancingAndContribution

        every { getBudgetTotalCost.getBudgetTotalCost(partner1Id) } returns totalCostPartner1
        every { getBudgetTotalCost.getBudgetTotalCost(partner2Id) } returns totalCostPartner2

        every { partnerBudgetPerFundCalculator.calculate(listOf(partner1, partner2), emptyList(), listOf(cof1, cof2))} returns listOf(result1, result2)

        Assertions.assertThat(getPartnerBudgetPerFund.getProjectPartnerBudgetPerFund(1, null))
            .containsExactlyInAnyOrder(
                result1, result2
            )
    }
}
