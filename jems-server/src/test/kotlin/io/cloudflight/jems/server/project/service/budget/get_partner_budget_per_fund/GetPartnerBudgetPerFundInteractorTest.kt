package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_fund

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

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
    private val beneficiary = ProjectPartnerSummary(
        id = 3L,
        active = true,
        abbreviation = "BEN 1",
        role = ProjectPartnerRole.LEAD_PARTNER,
        sortNumber = 1
    )

    @MockK
    lateinit var getPartnerBudgetPerFundService: GetPartnerBudgetPerFundService

    @InjectMockKs
    lateinit var getPartnerBudgetPerFund: GetPartnerBudgetPerFund

    @Test
    fun `get Budget CoFinancing for Standard Call`() {

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

        every { getPartnerBudgetPerFundService.getProjectPartnerBudgetPerFund(1, null) } returns listOf(result1, result2)

        assertThat(getPartnerBudgetPerFund.getProjectPartnerBudgetPerFund(1, null))
            .containsExactlyInAnyOrder(
                result1, result2
            )
    }

    @Test
    fun `get Budget CoFinancing for Small Project Funds Call`() {
        val projectId = 2L
        val version = "v1"

        val partnerBudgetPerFund = ProjectPartnerBudgetPerFund(
            beneficiary,
            budgetPerFund = emptySet(),
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            totalPartnerContribution = BigDecimal.ZERO,
            totalEligibleBudget = BigDecimal.ZERO,
            percentageOfTotalEligibleBudget = BigDecimal.ZERO
        )
        val spfBudgetPerFund = ProjectPartnerBudgetPerFund(
            beneficiary,
            budgetPerFund = emptySet(),
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            totalPartnerContribution = BigDecimal.ZERO,
            totalEligibleBudget = BigDecimal.ZERO,
            percentageOfTotalEligibleBudget = BigDecimal.ZERO
        )

        every { getPartnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, version) } returns listOf(partnerBudgetPerFund, spfBudgetPerFund)

        assertThat(getPartnerBudgetPerFund.getProjectPartnerBudgetPerFund(projectId, version))
            .containsExactlyInAnyOrder(partnerBudgetPerFund, spfBudgetPerFund)
    }
}
