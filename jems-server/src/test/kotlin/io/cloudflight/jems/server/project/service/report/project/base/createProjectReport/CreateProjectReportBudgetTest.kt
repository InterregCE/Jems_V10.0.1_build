package io.cloudflight.jems.server.project.service.report.project.base.createProjectReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.regular.PaymentToProject
import io.cloudflight.jems.server.payments.model.regular.PaymentType
import io.cloudflight.jems.server.payments.service.regular.PaymentRegularPersistence
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCertificateCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectPartnerBudgetPerFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.base.create.PreviouslyReportedFund
import io.cloudflight.jems.server.project.service.report.model.project.base.create.PreviouslyProjectReportedFund
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZonedDateTime

internal class CreateProjectReportBudgetTest : UnitTest() {

    private fun lumpSums(partnerId: Long) = listOf(
        ProjectLumpSum(
            orderNr = 14,
            programmeLumpSumId = 44L,
            period = 3,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(
                    partnerId = partnerId,
                    amount = BigDecimal.TEN,
                ),
            ),
            fastTrack = false,
            readyForPayment = false,
        ),
        ProjectLumpSum(
            orderNr = 15,
            programmeLumpSumId = 45L,
            period = 4,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(
                    partnerId = partnerId,
                    amount = BigDecimal.valueOf(13),
                ),
            ),
            fastTrack = true,
            readyForPayment = false,
        ),
        ProjectLumpSum(
            orderNr = 16,
            programmeLumpSumId = 45L,
            period = 4,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(
                    partnerId = partnerId,
                    amount = BigDecimal.valueOf(1033, 2),
                ),
            ),
            fastTrack = true,
            readyForPayment = true,
        ),
    )

    private fun partnerBudget(partner: ProjectPartnerSummary) = PartnerBudget(
        partner = partner,
        staffCosts = BigDecimal.valueOf(10),
        officeAndAdministrationCosts = BigDecimal.valueOf(11),
        travelCosts = BigDecimal.valueOf(12),
        externalCosts = BigDecimal.valueOf(13),
        equipmentCosts = BigDecimal.valueOf(14),
        infrastructureCosts = BigDecimal.valueOf(15),
        otherCosts = BigDecimal.valueOf(16),
        lumpSumContribution = BigDecimal.valueOf(17),
        unitCosts = BigDecimal.valueOf(18),
        totalCosts = BigDecimal.valueOf(19),
    )

    private val projectPartnerBudgetPerFund = ProjectPartnerBudgetPerFund(
        partner = null,
        costType = null,
        budgetPerFund = setOf(),
        publicContribution = BigDecimal.TEN,
        autoPublicContribution = BigDecimal.TEN,
        privateContribution = BigDecimal.TEN,
        totalPartnerContribution = BigDecimal(30),
        totalEligibleBudget = BigDecimal(50),
        percentageOfTotalEligibleBudget = BigDecimal(100)
    )

    private val currentTime = ZonedDateTime.now()

    private val payment = PaymentToProject(
        id = 1L,
        paymentType = PaymentType.FTLS,
        projectCustomIdentifier = "PR1",
        projectAcronym = "Test Project",
        paymentClaimNo = 0,
        fundName = "OTHER",
        fundId = 1L,
        amountApprovedPerFund = BigDecimal(100),
        amountPaidPerFund = BigDecimal.ZERO,
        paymentApprovalDate = currentTime,
        paymentClaimSubmissionDate = null,
        totalEligibleAmount = BigDecimal(10),
        lastApprovedVersionBeforeReadyForPayment = "v1.0"
    )

    private val cumulativeFund = ReportCertificateCoFinancingColumn(
        funds = mapOf(1L to BigDecimal(100), null to BigDecimal.valueOf(184L)),
        partnerContribution = BigDecimal.valueOf(24),
        publicContribution = BigDecimal.valueOf(25),
        automaticPublicContribution = BigDecimal.valueOf(26),
        privateContribution = BigDecimal.valueOf(27),
        sum = BigDecimal.valueOf(28),
    )

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var lumpSumPersistence: ProjectLumpSumPersistence

    @MockK
    lateinit var getProjectBudget: GetProjectBudget

    @MockK
    lateinit var reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistenceProvider

    @MockK
    lateinit var paymentPersistence: PaymentRegularPersistence

    @InjectMockKs
    lateinit var service: CreateProjectReportBudget

    @Test
    fun createReportBudget() {
        val projectId = 30L
        val version = "v4.2"

        every { reportPersistence.getSubmittedProjectReportIds(projectId) } returns setOf(1L)
        every { getProjectBudget.getBudget(projectId, version) } returns listOf(partnerBudget(ProjectPartnerSummary(
            id = 1L,
            abbreviation = "LP",
            active = true,
            role = ProjectPartnerRole.LEAD_PARTNER
        )))
        every { lumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums(1L)
        every { paymentPersistence.getPaymentsByProjectId(projectId) } returns listOf(payment)
        every { reportCertificateCoFinancingPersistence.getCoFinancingCumulative(setOf(1L)) } returns cumulativeFund

        val result = service.retrieveBudgetDataFor(projectId, version, projectPartnerBudgetPerFund)

        assertThat(result.previouslyReportedCoFinancing.previouslyReportedSum).isEqualTo(BigDecimal(38.33).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.previouslyReportedCoFinancing.previouslyReportedPartner).isEqualTo(BigDecimal(34.33).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.previouslyReportedCoFinancing.previouslyReportedPrivate).isEqualTo(BigDecimal(32.43).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.previouslyReportedCoFinancing.previouslyReportedPublic).isEqualTo(BigDecimal(30.43).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.previouslyReportedCoFinancing.previouslyReportedAutoPublic).isEqualTo(BigDecimal(31.43).setScale(2, RoundingMode.HALF_EVEN))
        assertThat(result.previouslyReportedCoFinancing.totalAutoPublic).isEqualTo(BigDecimal(10))
        assertThat(result.previouslyReportedCoFinancing.totalPartner).isEqualTo(BigDecimal(30))
        assertThat(result.previouslyReportedCoFinancing.totalPrivate).isEqualTo(BigDecimal(10))
        assertThat(result.previouslyReportedCoFinancing.totalPublic).isEqualTo(BigDecimal(10))
        assertThat(result.previouslyReportedCoFinancing.totalSum).isEqualTo(BigDecimal(19))
        assertThat(result.previouslyReportedCoFinancing.fundsSorted).containsExactlyInAnyOrder(
            PreviouslyProjectReportedFund(
                fundId = 1L,
                percentage = BigDecimal(0),
                total = BigDecimal(0),
                previouslyReported = BigDecimal(100).setScale(2, RoundingMode.HALF_EVEN),
                previouslyPaid = BigDecimal(0)
            ),
            PreviouslyProjectReportedFund(
                fundId = null,
                percentage = BigDecimal(100),
                total = BigDecimal(30),
                previouslyReported = BigDecimal(34.33).setScale(2, RoundingMode.HALF_EVEN),
                previouslyPaid = BigDecimal(0)
            )
        )
    }
}
