package io.cloudflight.jems.server.project.service.report.partner.base.finalizeControlPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCurrentValuesWrapper
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport.SubmitProjectPartnerReportTest.Companion.fund
import io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport.SubmitProjectPartnerReportTest.Companion.options
import io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport.SubmitProjectPartnerReportTest.Companion.partnerContribution
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.overview.ProjectPartnerReportControlOverviewPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.overview.runControlPartnerReportPreSubmissionCheck.RunControlPartnerReportPreSubmissionCheckService
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectPartnerReportDesignatedControllerPersistence
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
            partnerAbbreviation = "LP-1",
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerId = PARTNER_ID
        )

        private val expectedCostCategoryWithParked = BudgetCostsCurrentValuesWrapper(
            currentlyReported = BudgetCostsCalculationResultFull(
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
            ),
            currentlyReportedParked = BudgetCostsCalculationResultFull(
                staff = BigDecimal.ZERO,
                office = BigDecimal.valueOf(0, 2),
                travel = BigDecimal.valueOf(0, 2),
                external = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructure = BigDecimal.ZERO,
                other = BigDecimal.ZERO,
                lumpSum = BigDecimal.valueOf(10, 0),
                unitCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(1000, 2),
            )
        )

        private val expectedCoFin = ReportExpenditureCoFinancingColumn(
            funds = mapOf(
                29L to BigDecimal.valueOf(5174, 2),
                35L to BigDecimal.valueOf(23380, 2),
                null to BigDecimal.valueOf(8487, 2),
            ),
            partnerContribution = BigDecimal.valueOf(8487, 2),
            publicContribution = BigDecimal.valueOf(2222, 2),
            automaticPublicContribution = BigDecimal.valueOf(2963, 2),
            privateContribution = BigDecimal.valueOf(3704, 2),
            sum = BigDecimal.valueOf(37041, 2),
        )

        private val expectedParkedCoFin = ReportExpenditureCoFinancingColumn(
            funds = mapOf(
                29L to BigDecimal.valueOf(139, 2),
                35L to BigDecimal.valueOf(631, 2),
                null to BigDecimal.valueOf(230, 2),
            ),
            partnerContribution = BigDecimal.valueOf(230, 2),
            publicContribution = BigDecimal.valueOf(60, 2),
            automaticPublicContribution = BigDecimal.valueOf(80, 2),
            privateContribution = BigDecimal.valueOf(100, 2),
            sum = BigDecimal.valueOf(1000, 2),
        )

        private val expenditure1 = ProjectPartnerReportExpenditureVerification(
            id = 630,
            number = 1,
            lumpSumId = null,
            unitCostId = 18L,
            costCategory = ReportBudgetCategory.StaffCosts,
            gdpr = false,
            investmentId = 10L,
            contractId = 54L,
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1),
            dateOfPayment = LocalDate.of(2022, 2, 1),
            numberOfUnits = BigDecimal.ZERO,
            pricePerUnit = BigDecimal.TEN, /* not needed */
            declaredAmount = BigDecimal.TEN, /* not needed */
            currencyCode = "CZK",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
            partOfSample = false,
            certifiedAmount = BigDecimal.valueOf(25448, 2),
            deductedAmount = BigDecimal.ZERO,
            typologyOfErrorId = null,
            verificationComment = null,
            parked = false,
            parkedOn = null,
            parkingMetadata = null,
            partOfSampleLocked = false
        )

        private val expenditure2 = ProjectPartnerReportExpenditureVerification(
            id = 631,
            number = 2,
            lumpSumId = 22L,
            unitCostId = null,
            costCategory = ReportBudgetCategory.Multiple,
            gdpr = true,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = null,
            dateOfPayment = null,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.TEN, /* not needed */
            declaredAmount = BigDecimal.TEN, /* not needed */
            currencyCode = "EUR",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
            partOfSample = false,
            certifiedAmount = BigDecimal.valueOf(485, 1),
            deductedAmount = BigDecimal.ZERO,
            typologyOfErrorId = null,
            verificationComment = null,
            parked = false,
            parkedOn = null,
            parkingMetadata = null,
            partOfSampleLocked = false
        )

        private val controlOverview = ControlOverview(
            startDate = LocalDate.now(),
            requestsForClarifications = "test",
            receiptOfSatisfactoryAnswers = "test",
            endDate = LocalDate.now(),
            findingDescription = "test",
            followUpMeasuresFromLastReport = "test",
            conclusion = "test",
            followUpMeasuresForNextReport = "test"
        )

        private val controllerInstitution = ControllerInstitutionList(
            id = 1,
            name = "Test institution",
            description = null,
            institutionNuts = emptyList(),
            createdAt = ZonedDateTime.now()
        )
    }

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    private lateinit var preSubmissionCheckService: RunControlPartnerReportPreSubmissionCheckService

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
    private lateinit var controlOverviewPersistence: ProjectPartnerReportControlOverviewPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var controlInstitutionPersistence: ControllerInstitutionPersistence

    @MockK
    private lateinit var reportDesignatedControllerPersistence: ProjectPartnerReportDesignatedControllerPersistence

    @MockK
    private lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    private lateinit var interactor: FinalizeControlPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, preSubmissionCheckService, partnerPersistence, reportControlExpenditurePersistence,
            reportExpenditureCostCategoryPersistence, reportExpenditureCoFinancingPersistence, reportContributionPersistence,
            reportLumpSumPersistence, reportUnitCostPersistence, reportInvestmentPersistence, controlOverviewPersistence,
            auditPublisher, controlInstitutionPersistence, reportDesignatedControllerPersistence, projectPersistence)
    }

    @ParameterizedTest(name = "finalizeControl (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "ReOpenCertified"])
    fun finalizeControl(status: ReportStatus) {
        val report = report(42L, status)
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 42L) } returns report
        every { preSubmissionCheckService.preCheck(PARTNER_ID, 42L).isSubmissionAllowed } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "5.6.1") } returns PROJECT_ID

        every {
            reportControlExpenditurePersistence.getPartnerControlReportExpenditureVerification(
                PARTNER_ID,
                reportId = 42L
            )
        } returns
                listOf(
                    expenditure1, expenditure2.copy(
                        declaredAmountAfterSubmission = BigDecimal.TEN,
                        parkingMetadata = ExpenditureParkingMetadata(
                            reportOfOriginId = 70L,
                            reportOfOriginNumber = 5,
                            reportProjectOfOriginId = null,
                            originalExpenditureNumber = 3
                        ),
                        parked = true
                    )
                )

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 42L) } returns options
        every { controlOverviewPersistence.updatePartnerControlReportOverviewEndDate(PARTNER_ID, 42L, LocalDate.now()) } returns controlOverview

        val slotCostCategory = slot<BudgetCostsCurrentValuesWrapper>()
        every { reportExpenditureCostCategoryPersistence.updateAfterControlValues(PARTNER_ID, reportId = 42L, capture(slotCostCategory)) } answers { }
        every { reportDesignatedControllerPersistence.updateWithInstitutionName(PARTNER_ID, reportId = 42, controllerInstitution.name) } returns Unit
        every { controlInstitutionPersistence.getControllerInstitutions(setOf(PARTNER_ID)) } returns mapOf(Pair(PARTNER_ID, controllerInstitution))

        val slotCostCoFin = slot<ExpenditureCoFinancingCurrent>()
        every { reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, reportId = 42L) } returns partnerContribution()
        every { reportExpenditureCoFinancingPersistence.updateAfterControlValues(PARTNER_ID, reportId = 42L, capture(slotCostCoFin)) } answers { }

        val slotLumpSum = slot<Map<Long, ExpenditureLumpSumCurrent>>()
        every { reportLumpSumPersistence.updateAfterControlValues(PARTNER_ID, reportId = 42L, capture(slotLumpSum)) } answers { }
        val slotUnitCost = slot<Map<Long, ExpenditureUnitCostCurrent>>()
        every { reportUnitCostPersistence.updateAfterControlValues(PARTNER_ID, reportId = 42L, capture(slotUnitCost)) } answers { }
        val slotInvestment = slot<Map<Long, ExpenditureInvestmentCurrent>>()
        every {
            reportInvestmentPersistence.updateAfterControlValues(
                PARTNER_ID,
                reportId = 42L,
                capture(slotInvestment),
            )
        } answers { }

        every { reportPersistence.finalizeControlOnReportById(any(), any(), any()) } returns mockedResult
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns mockk()

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { auditPublisher.publishEvent(ofType(PartnerReportStatusChanged::class)) } returns Unit

        assertThat(interactor.finalizeControl(PARTNER_ID, 42L)).isEqualTo(ReportStatus.Certified)

        verify(exactly = 1) { reportPersistence.finalizeControlOnReportById(PARTNER_ID, 42L, any()) }

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_REPORT_CONTROL_FINALIZED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("FG01_654")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(42L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "[LP1] Control for partner report R.7 is finalized and the following items were parked by control: [R5.3]"
        )

        assertThat(slotCostCategory.captured).isEqualTo(expectedCostCategoryWithParked)
        assertThat(slotCostCoFin.captured).isEqualTo(ExpenditureCoFinancingCurrent(expectedCoFin, expectedParkedCoFin))
        assertThat(slotUnitCost.captured).containsExactlyEntriesOf(
            mapOf(
                Pair(
                    18L,
                    ExpenditureUnitCostCurrent(current = BigDecimal.valueOf(25448, 2), currentParked = BigDecimal.ZERO)
                )
            )
        )
        assertThat(slotLumpSum.captured).containsExactlyEntriesOf(
            mapOf(
                Pair(
                    22L,
                    ExpenditureLumpSumCurrent(current = BigDecimal.valueOf(485, 1), currentParked = BigDecimal.TEN)
                )
            )
        )
        assertThat(slotInvestment.captured).containsExactlyEntriesOf(
            mapOf(
                Pair(
                    10L,
                    ExpenditureInvestmentCurrent(
                        current = BigDecimal.valueOf(25448, 2),
                        currentParked = BigDecimal.ZERO
                    )
                )
            )
        )
    }

    @ParameterizedTest(name = "finalizeControl - wrong status (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "ReOpenCertified"], mode = EnumSource.Mode.EXCLUDE)
    fun `finalizeControl - wrong status`(status: ReportStatus) {
        val report = report(44L, status)
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 44L) } returns report

        assertThrows<ReportNotInControl> { interactor.finalizeControl(PARTNER_ID, 44L) }

        verify(exactly = 0) { reportPersistence.finalizeControlOnReportById(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @ParameterizedTest(name = "finalizeControl - fails pre-check (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["InControl", "ReOpenCertified"])
    fun `finalizeControl - fails pre-check`(status: ReportStatus) {
        val report = report(46L, status)
        every { reportPersistence.getPartnerReportById(PARTNER_ID, 46L) } returns report
        every { preSubmissionCheckService.preCheck(PARTNER_ID, 46L).isSubmissionAllowed } returns false

        assertThrows<SubmissionNotAllowed> { interactor.finalizeControl(PARTNER_ID, 46L) }

        verify(exactly = 0) { reportPersistence.finalizeControlOnReportById(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    private fun report(id: Long, status: ReportStatus): ProjectPartnerReport {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns id
        every { report.status } returns status
        every { report.identification.coFinancing } returns listOf(
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund(id = 29L),
                percentage = BigDecimal.valueOf(1397, 2)
            ),
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund(id = 35L),
                percentage = BigDecimal.valueOf(6312, 2)
            ),
            ProjectPartnerCoFinancing(
                ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                null,
                percentage = BigDecimal.valueOf(2291, 2)
            ),
        )
        return report
    }
}
