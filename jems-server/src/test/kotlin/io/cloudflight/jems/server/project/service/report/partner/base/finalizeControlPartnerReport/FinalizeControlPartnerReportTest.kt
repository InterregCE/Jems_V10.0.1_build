package io.cloudflight.jems.server.project.service.report.partner.base.finalizeControlPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport.SubmitProjectPartnerReportTest.Companion.fund
import io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport.SubmitProjectPartnerReportTest.Companion.options
import io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport.SubmitProjectPartnerReportTest.Companion.partnerContribution
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

internal class FinalizeControlPartnerReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 256L
        private const val PARTNER_ID = 581L

        private val mockedResult = ProjectPartnerReportSubmissionSummary(
            id = 42L,
            reportNumber = 7,
            status = ReportStatus.Certified,
            version = "5.6.1",
            // not important
            firstSubmission = ZonedDateTime.now(),
            controlEnd = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
        )

        private val expectedCostCategory = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(25448, 2),
            office = BigDecimal.valueOf(2926, 2),
            travel = BigDecimal.valueOf(3817, 2),
            external = BigDecimal.ZERO,
            equipment = BigDecimal.ZERO,
            infrastructure = BigDecimal.ZERO,
            other = BigDecimal.ZERO,
            lumpSum = BigDecimal.valueOf(485, 1),
            unitCost = BigDecimal.ZERO,
            sum = BigDecimal.valueOf(37041, 2),
        )

        private val expectedCoFin = ReportExpenditureCoFinancingColumn(
            funds = mapOf(
                29L to BigDecimal.valueOf(5174, 2),
                35L to BigDecimal.valueOf(23380, 2),
                null to BigDecimal.valueOf(8486, 2),
            ),
            partnerContribution = BigDecimal.valueOf(8486, 2),
            publicContribution = BigDecimal.valueOf(2222, 2),
            automaticPublicContribution = BigDecimal.valueOf(2963, 2),
            privateContribution = BigDecimal.valueOf(3704, 2),
            sum = BigDecimal.valueOf(37041, 2),
        )

        private val expenditure1 = ProjectPartnerReportExpenditureVerification(
            id = 630,
            lumpSumId = null,
            unitCostId = 18L,
            costCategory = ReportBudgetCategory.StaffCosts,
            investmentId = 10L,
            contractId = 54L,
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1),
            dateOfPayment = LocalDate.of(2022, 2, 1),
            numberOfUnits = BigDecimal.ZERO,
            pricePerUnit = BigDecimal.TEN /* not needed */,
            declaredAmount = BigDecimal.TEN /* not needed */,
            currencyCode = "CZK",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
            partOfSample = false,
            certifiedAmount = BigDecimal.valueOf(25448, 2),
            deductedAmount = BigDecimal.ZERO,
            typologyOfErrorId = null,
            verificationComment = null,
        )

        private val expenditure2 = ProjectPartnerReportExpenditureVerification(
            id = 631,
            lumpSumId = 22L,
            unitCostId = null,
            costCategory = ReportBudgetCategory.Multiple,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = null,
            dateOfPayment = null,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.TEN /* not needed */,
            declaredAmount = BigDecimal.TEN /* not needed */,
            currencyCode = "EUR",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
            partOfSample = false,
            certifiedAmount = BigDecimal.valueOf(485, 1),
            deductedAmount = BigDecimal.ZERO,
            typologyOfErrorId = null,
            verificationComment = null,
        )

    }

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    private lateinit var partnerPersistence: PartnerPersistence
    @MockK
    private lateinit var reportControlExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence
    @MockK
    private lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence
    @MockK
    private lateinit var reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence
    @MockK
    private lateinit var reportContributionPersistence: ProjectPartnerReportContributionPersistence
    @MockK
    private lateinit var reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence
    @MockK
    private lateinit var reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence
    @MockK
    private lateinit var reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var interactor: FinalizeControlPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(partnerPersistence)
        clearMocks(auditPublisher)
    }

    @ParameterizedTest(name = "finalizeControl (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"])
    fun finalizeControl(status: ReportStatus) {
        val report = report(42L, status)
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 42L) } returns report
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "5.6.1") } returns PROJECT_ID

        every { reportControlExpenditurePersistence.getPartnerControlReportExpenditureVerification(PARTNER_ID, reportId = 42L) } returns
            listOf(expenditure1, expenditure2)

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 42L) } returns options

        val slotCostCategory = slot<BudgetCostsCalculationResultFull>()
        every { reportExpenditureCostCategoryPersistence.updateAfterControlValues(PARTNER_ID, reportId = 42L, capture(slotCostCategory)) } answers { }

        val slotCostCoFin = slot<ReportExpenditureCoFinancingColumn>()
        every { reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, reportId = 42L) } returns partnerContribution()
        every { reportExpenditureCoFinancingPersistence.updateAfterControlValues(PARTNER_ID, reportId = 42L, capture(slotCostCoFin)) } answers { }

        val slotLumpSum = slot<Map<Long, BigDecimal>>()
        every { reportLumpSumPersistence.updateAfterControlValues(PARTNER_ID, reportId = 42L, capture(slotLumpSum)) } answers { }
        val slotUnitCost = slot<Map<Long, BigDecimal>>()
        every { reportUnitCostPersistence.updateAfterControlValues(PARTNER_ID, reportId = 42L, capture(slotUnitCost)) } answers { }
        val slotInvestment = slot<Map<Long, BigDecimal>>()
        every { reportInvestmentPersistence.updateAfterControlValues(PARTNER_ID, reportId = 42L, capture(slotInvestment)) } answers { }

        every { reportPersistence.finalizeControlOnReportById(any(), any(), any()) } returns mockedResult

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        assertThat(interactor.finalizeControl(PARTNER_ID, 42L)).isEqualTo(ReportStatus.Certified)

        verify(exactly = 1) { reportPersistence.finalizeControlOnReportById(PARTNER_ID, 42L, any()) }

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_REPORT_CONTROL_FINALIZED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("FG01_654")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(42L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[FG01_654] [LP1] Partner report R.7 control work finalized")

        assertThat(slotCostCategory.captured).isEqualTo(expectedCostCategory)
        assertThat(slotCostCoFin.captured).isEqualTo(expectedCoFin)
        assertThat(slotUnitCost.captured).containsExactlyEntriesOf(mapOf(Pair(18L, BigDecimal.valueOf(25448, 2))))
        assertThat(slotLumpSum.captured).containsExactlyEntriesOf(mapOf(Pair(22L, BigDecimal.valueOf(485, 1))))
        assertThat(slotInvestment.captured).containsExactlyEntriesOf(mapOf(Pair(10L, BigDecimal.valueOf(25448, 2))))
    }

    @ParameterizedTest(name = "finalizeControl - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl"], mode = EnumSource.Mode.EXCLUDE)
    fun `finalizeControl - wrong status`(status: ReportStatus) {
        val report = report(44L, status)
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 44L) } returns report

        assertThrows<ReportNotInControl> { interactor.finalizeControl(PARTNER_ID, 44L) }

        verify(exactly = 0) { reportPersistence.finalizeControlOnReportById(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    private fun report(id: Long, status: ReportStatus): ProjectPartnerReport {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns id
        every { report.status } returns status
        every { report.identification.coFinancing } returns listOf(
            ProjectPartnerCoFinancing(ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund(id = 29L), percentage = BigDecimal.valueOf(1397, 2)),
            ProjectPartnerCoFinancing(ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund(id = 35L), percentage = BigDecimal.valueOf(6312, 2)),
            ProjectPartnerCoFinancing(ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution, null, percentage = BigDecimal.valueOf(2291, 2)),
        )
        return report
    }
}
