package io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_fund

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.project.repository.partner.cofinancing.ProjectPartnerCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetCoFinancing
import io.cloudflight.jems.server.project.service.cofinancing.model.PartnerBudgetSpfCoFinancing
import io.cloudflight.jems.server.project.service.common.PartnerBudgetPerFundCalculatorService
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.budget.get_budget_total_cost.GetBudgetTotalCost
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.toScaledBigDecimal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
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
    private val beneficiary = ProjectPartnerSummary(
        id = 3L,
        active = true,
        abbreviation = "BEN 1",
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
        type = CallType.STANDARD,
        startDate = ZonedDateTime.now(),
        endDateStep1 = ZonedDateTime.now(),
        endDate = ZonedDateTime.now(),
        isAdditionalFundAllowed = true,
        lengthOfPeriod = null,
        applicationFormFieldConfigurations = mutableSetOf(),
        preSubmissionCheckPluginKey = null,
        firstStepPreSubmissionCheckPluginKey = null
    )
    private val spfCall = CallDetail(
        id = 2,
        name = "spf call",
        status = CallStatus.PUBLISHED,
        type = CallType.SPF,
        startDate = ZonedDateTime.now(),
        endDateStep1 = ZonedDateTime.now(),
        endDate = ZonedDateTime.now(),
        isAdditionalFundAllowed = true,
        lengthOfPeriod = 2,
        applicationFormFieldConfigurations = mutableSetOf(),
        preSubmissionCheckPluginKey = null,
        firstStepPreSubmissionCheckPluginKey = null
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

        every { partnerBudgetPerFundCalculator.calculate(listOf(partner1, partner2), emptyList(), listOf(cof1, cof2), null)} returns listOf(result1, result2)

        assertThat(getPartnerBudgetPerFund.getProjectPartnerBudgetPerFund(1, null))
            .containsExactlyInAnyOrder(
                result1, result2
            )
    }

    @Test
    fun `get Budget CoFinancing for Small Project Funds Call`() {
        val projectId = 2L
        val partnerId = beneficiary.id!!
        val version = "v1"
        val totalCostManagement = 15.toScaledBigDecimal()
        val totalCostSpf = 15.toScaledBigDecimal()

        val coFinancingAndContributionSpf = ProjectPartnerCoFinancingAndContributionSpf(
            finances = emptyList(),
            partnerContributions = emptyList()
        )
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

        val coFinancing = PartnerBudgetCoFinancing(
            partner = beneficiary,
            projectPartnerCoFinancingAndContribution =
            ProjectPartnerCoFinancingAndContribution(
                finances = emptyList(),
                partnerContributions = emptyList(),
                partnerAbbreviation = ""
            ),
            total = totalCostManagement
        )
        val coFinancingSpf = PartnerBudgetSpfCoFinancing(
            partner = beneficiary,
            projectPartnerCoFinancingAndContribution =
            ProjectPartnerCoFinancingAndContributionSpf(
                finances = emptyList(),
                partnerContributions = emptyList()
            ),
            total = totalCostSpf
        )

        every { projectBudgetPersistence.getPartnersForProjectId(projectId, version) } returns listOf(beneficiary)
        every { callPersistence.getCallByProjectId(projectId) } returns spfCall

        every { projectPartnerCoFinancingPersistenceProvider
            .getCoFinancingAndContributions(partnerId, version) } returns projectPartnerCoFinancingAndContribution
        every { getBudgetTotalCost.getBudgetTotalCost(partnerId, version) } returns totalCostManagement

        every { projectPartnerCoFinancingPersistenceProvider.getSpfCoFinancingAndContributions(partnerId, version) } returns coFinancingAndContributionSpf
        every { getBudgetTotalCost.getBudgetTotalSpfCost(partnerId, version) } returns totalCostSpf

        every { partnerBudgetPerFundCalculator
            .calculate(listOf(beneficiary), emptyList(), listOf(coFinancing), coFinancingSpf)
        } returns listOf(partnerBudgetPerFund, spfBudgetPerFund)

        assertThat(getPartnerBudgetPerFund.getProjectPartnerBudgetPerFund(projectId, version))
            .containsExactlyInAnyOrder(partnerBudgetPerFund, spfBudgetPerFund)
    }
}
