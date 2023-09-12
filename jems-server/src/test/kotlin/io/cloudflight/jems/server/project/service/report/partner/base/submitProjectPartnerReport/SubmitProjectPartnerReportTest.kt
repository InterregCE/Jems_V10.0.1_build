package io.cloudflight.jems.server.project.service.report.partner.base.submitProjectPartnerReport

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportSamplingCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.ControlReportSamplingCheckResult
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.currency.repository.CurrencyPersistence
import io.cloudflight.jems.server.currency.service.model.CurrencyConversion
import io.cloudflight.jems.server.notification.handler.PartnerReportStatusChanged
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryCurrentlyReportedWithReIncluded
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus.*
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCurrencyRateChange
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.base.runPartnerReportPreSubmissionCheck.RunPartnerReportPreSubmissionCheckService
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.VerificationAction
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.UUID

internal class SubmitProjectPartnerReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 256L
        private const val PARTNER_ID = 579L
        private val TODAY = LocalDate.now()
        private val YEAR = TODAY.year
        private val MONTH = TODAY.monthValue

        private val mockedResult = ProjectPartnerReportSubmissionSummary(
            id = 888L,
            reportNumber = 4,
            status = ReportStatus.Submitted,
            version = "5.6.0",
            // not important
            firstSubmission = ZonedDateTime.now(),
            controlEnd = null,
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "FG01_654",
            projectAcronym = "acronym",
            partnerAbbreviation = "P-1",
            partnerNumber = 1,
            partnerRole = ProjectPartnerRole.PARTNER,
            partnerId = PARTNER_ID
        )

        private val expenditure1 = ProjectPartnerReportExpenditureCost(
            id = 630,
            number = 1,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            gdpr = false,
            investmentId = 10L,
            contractId = 54L,
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1),
            dateOfPayment = LocalDate.of(2022, 2, 1),
            numberOfUnits = BigDecimal.ZERO,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.valueOf(25448, 2),
            currencyCode = "CZK",
            currencyConversionRate = BigDecimal.valueOf(33333L, 4),
            declaredAmountAfterSubmission = null,
            attachment = null,
            parkingMetadata = null,
        )

        private val expenditure2 = ProjectPartnerReportExpenditureCost(
            id = 631,
            number = 2,
            lumpSumId = 22L,
            unitCostId = null,
            costCategory = ReportBudgetCategory.Multiple,
            gdpr = false,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = null,
            dateOfPayment = null,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.valueOf(485, 1),
            declaredAmount = BigDecimal.valueOf(485, 1),
            currencyCode = "EUR",
            currencyConversionRate = BigDecimal.valueOf(77895L, 4),
            declaredAmountAfterSubmission = BigDecimal.valueOf(623L, 2),
            attachment = null,
            parkingMetadata = ExpenditureParkingMetadata(
                reportOfOriginId = 70L,
                reportOfOriginNumber = 5,
                reportProjectOfOriginId = null,
                originalExpenditureNumber = 3
            ),
        )

        private val expenditure3 = ProjectPartnerReportExpenditureCost(
            id = 632,
            number = 3,
            lumpSumId = null,
            unitCostId = 15L,
            costCategory = ReportBudgetCategory.InfrastructureCosts,
            gdpr = true,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = null,
            invoiceNumber = null,
            invoiceDate = null,
            dateOfPayment = null,
            numberOfUnits = BigDecimal.ONE,
            pricePerUnit = BigDecimal.valueOf(165, 1),
            declaredAmount = BigDecimal.valueOf(165, 1),
            currencyCode = "EUR",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
            parkingMetadata = null,
        )

        val options = mockk<ReportExpenditureCostCategory>().also {
            every { it.options } returns ProjectPartnerBudgetOptions(
                partnerId = PARTNER_ID,
                officeAndAdministrationOnStaffCostsFlatRate = null,
                officeAndAdministrationOnDirectCostsFlatRate = 10,
                travelAndAccommodationOnStaffCostsFlatRate = 15,
                staffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
            )
            every { it.totalsFromAF.sum } returns BigDecimal.valueOf(500L)
        }

        private val expectedPersistedExpenditureCostCategory = ExpenditureCostCategoryCurrentlyReportedWithReIncluded(
            currentlyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(999, 2),
                office = BigDecimal.valueOf(279, 2),
                travel = BigDecimal.valueOf(149, 2),
                external = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructure = BigDecimal.valueOf(165L, 1),
                other = BigDecimal.ZERO,
                lumpSum = BigDecimal.valueOf(623L, 2),
                unitCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(3700L, 2),
            ),
            currentlyReportedReIncluded = BudgetCostsCalculationResultFull(
                staff = BigDecimal.ZERO,
                office = BigDecimal.valueOf(0L, 2),
                travel = BigDecimal.valueOf(0L, 2),
                external = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructure = BigDecimal.ZERO,
                other = BigDecimal.ZERO,
                lumpSum = BigDecimal.valueOf(623L, 2),
                unitCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(623L, 2),
            )
        )

        fun fund(id: Long): ProgrammeFund {
            val fundMock = mockk<ProgrammeFund>()
            every { fundMock.id } returns id
            return fundMock
        }

        private fun contrib(
            status: ProjectPartnerContributionStatus,
            amount: BigDecimal,
            prev: BigDecimal,
            current: BigDecimal,
        ) = ProjectPartnerReportEntityContribution(
            legalStatus = status,
            amount = amount,
            previouslyReported = prev,
            currentlyReported = current,
            /* not important: */
            attachment = null,
            createdInThisReport = true,
            historyIdentifier = UUID.randomUUID(),
            id = 0L,
            idFromApplicationForm = null,
            sourceOfContribution = null,
        )

        fun partnerContribution() = listOf(
            contrib(Public, amount = BigDecimal.valueOf(30), prev = BigDecimal.valueOf(3), current = BigDecimal.valueOf(9)),
            contrib(AutomaticPublic, amount = BigDecimal.valueOf(40), prev = BigDecimal.valueOf(4), current = BigDecimal.valueOf(13)),
            contrib(Private, amount = BigDecimal.valueOf(50), prev = BigDecimal.valueOf(5), current = BigDecimal.valueOf(20)),
        )

        private val expectedCoFinancing = ReportExpenditureCoFinancingColumn(
            funds = mapOf(
                29L to BigDecimal.valueOf(516L, 2),
                35L to BigDecimal.valueOf(2335L, 2),
                null to BigDecimal.valueOf(849L, 2),
            ),
            partnerContribution = BigDecimal.valueOf(849L, 2),
            publicContribution = BigDecimal.valueOf(222L, 2),
            automaticPublicContribution = BigDecimal.valueOf(296L, 2),
            privateContribution = BigDecimal.valueOf(370L, 2),
            sum = BigDecimal.valueOf(3700L, 2),
        )

        private val expectedReIncludedCoFinancing = ReportExpenditureCoFinancingColumn(
            funds = mapOf(
                29L to BigDecimal.valueOf(87L, 2),
                35L to BigDecimal.valueOf(393L, 2),
                null to BigDecimal.valueOf(143L, 2),
            ),
            partnerContribution = BigDecimal.valueOf(143L, 2),
            publicContribution = BigDecimal.valueOf(37L, 2),
            automaticPublicContribution = BigDecimal.valueOf(49L, 2),
            privateContribution = BigDecimal.valueOf(62L, 2),
            sum = BigDecimal.valueOf(623L, 2),
        )

        private val coFinancing = listOf(
            ProjectPartnerCoFinancing(MainFund, fund(id = 29L), percentage = BigDecimal.valueOf(1397, 2)),
            ProjectPartnerCoFinancing(MainFund, fund(id = 35L), percentage = BigDecimal.valueOf(6312, 2)),
            ProjectPartnerCoFinancing(PartnerContribution, null, percentage = BigDecimal.valueOf(2291, 2)),
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var preSubmissionCheck: RunPartnerReportPreSubmissionCheckService

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence

    @MockK
    lateinit var currencyPersistence: CurrencyPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence

    @MockK
    lateinit var reportExpenditureCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence

    @MockK
    lateinit var reportContributionPersistence: ProjectPartnerReportContributionPersistence

    @MockK
    lateinit var reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence

    @MockK
    lateinit var reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence

    @MockK
    lateinit var reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence

    @MockK
    lateinit var reportExpenditureVerificationPersistence: ProjectPartnerReportExpenditureVerificationPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    private lateinit var jemsPluginRegistry: JemsPluginRegistry

    @MockK
    private lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var submitReport: SubmitProjectPartnerReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, reportExpenditurePersistence, reportExpenditureVerificationPersistence, auditPublisher)
    }

    @ParameterizedTest(name = "submit - clear verification {0} to {2}")
    @CsvSource(value = [
        "Draft,false,Submitted,true",
        "ReOpenSubmittedLast,false,Submitted,true",
        "ReOpenSubmittedLimited,false,Submitted,false",
        "ReOpenInControlLast,false,InControl,true",
        "ReOpenInControlLimited,false,InControl,false",
        "ReOpenInControlLast,true,ReOpenCertified,true",
        "ReOpenInControlLimited,true,ReOpenCertified,false",
    ])
    fun `submit - clear verification`(
        currentStatus: ReportStatus,
        controlReopenedBefore: Boolean,
        expectedNewStatus: ReportStatus,
        shouldUpdateFinancing: Boolean,
    ) {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns currentStatus
        every { report.id } returns 35L
        every { report.lastControlReopening } returns if (controlReopenedBefore) mockk() else null
        every { report.identification.coFinancing } returns coFinancing

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 35L) } returns report
        every { preSubmissionCheck.preCheck(PARTNER_ID, reportId = 35L) } returns PreConditionCheckResult(emptyList(), true)

        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, 35L) } returns
                listOf(
                    expenditure1 /* CZK */,
                    expenditure2 /* EUR, but re-included */,
                    expenditure3 /* EUR */,
                )
        every { currencyPersistence.findAllByIdYearAndIdMonth(year = YEAR, month = MONTH) } returns
                listOf(
                    CurrencyConversion("CZK", YEAR, MONTH, "", BigDecimal.valueOf(254855, 4)),
                    CurrencyConversion("PLN", YEAR, MONTH, "", BigDecimal.valueOf(195, 2)), /* not used */
                    CurrencyConversion("EUR", YEAR, MONTH, "", BigDecimal.ONE),
                )

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 35L) } returns options
        val expenditureCcSlot = slot<ExpenditureCostCategoryCurrentlyReportedWithReIncluded>()
        every {
            reportExpenditureCostCategoryPersistence
                .updateCurrentlyReportedValues(PARTNER_ID, reportId = 35L, capture(expenditureCcSlot))
        } answers { }

        every { reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, reportId = 35L) } returns partnerContribution()
        val coFinSlot = slot<ExpenditureCoFinancingCurrentWithReIncluded>()
        every { reportExpenditureCoFinancingPersistence.updateCurrentlyReportedValues(PARTNER_ID, reportId = 35L, capture(coFinSlot)) } answers { }

        val lumpSumSlot = slot<Map<Long, ExpenditureLumpSumCurrentWithReIncluded>>()
        every { reportLumpSumPersistence.updateCurrentlyReportedValues(PARTNER_ID, reportId = 35L, capture(lumpSumSlot)) } answers { }

        val unitCostSlot = slot<Map<Long, ExpenditureUnitCostCurrentWithReIncluded>>()
        every { reportUnitCostPersistence.updateCurrentlyReportedValues(PARTNER_ID, reportId = 35L, capture(unitCostSlot)) } answers { }

        val investmentSlot = slot<Map<Long, ExpenditureInvestmentCurrentWithReIncluded>>()
        every { reportInvestmentPersistence.updateCurrentlyReportedValues(PARTNER_ID, reportId = 35L, capture(investmentSlot)) } answers { }

        val submissionTime = slot<ZonedDateTime>()
        if (currentStatus == ReportStatus.Draft)
            every { reportPersistence.updateStatusAndTimes(PARTNER_ID, 35L, expectedNewStatus, capture(submissionTime), null, null) } returns mockedResult
        else
            every { reportPersistence.updateStatusAndTimes(PARTNER_ID, 35L, expectedNewStatus, null, capture(submissionTime), null) } returns mockedResult

        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "5.6.0") } returns PROJECT_ID
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns mockk()

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { auditPublisher.publishEvent(ofType(PartnerReportStatusChanged::class)) } returns Unit

        val slotRates = slot<Collection<ProjectPartnerReportExpenditureCurrencyRateChange>>()
        val slotVerificationPreparationAction = slot<VerificationAction>()
        every {
            reportExpenditureVerificationPersistence
                .updateCurrencyRatesAndPrepareVerification(
                    35L,
                    capture(slotRates),
                    capture(slotVerificationPreparationAction),
                )

        } returns listOf(
            expenditure1.copy(currencyConversionRate = BigDecimal.valueOf(254855, 4), declaredAmountAfterSubmission = BigDecimal.valueOf(999L, 2)),
            expenditure2.copy(
                currencyConversionRate = BigDecimal.valueOf(77895, 4),
                declaredAmountAfterSubmission = BigDecimal.valueOf(623L, 2)
                ), // re-included
            expenditure3.copy(currencyConversionRate = BigDecimal.ONE, declaredAmountAfterSubmission = BigDecimal.valueOf(165, 1)),
        )

        every { callPersistence.getCallSimpleByPartnerId(PARTNER_ID).controlReportSamplingCheckPluginKey} returns "plugin-key"
        val plugin = mockk<ControlReportSamplingCheckPlugin>()
        every { plugin.check(PARTNER_ID, 35L) } returns ControlReportSamplingCheckResult(setOf(21L))
        every { jemsPluginRegistry.get(ControlReportSamplingCheckPlugin::class, "plugin-key")} returns plugin
        every { reportExpenditurePersistence.markAsSampledAndLock(setOf(21L)) } answers { }

        submitReport.submit(PARTNER_ID, 35L)

        verify(exactly = 1) { reportPersistence.updateStatusAndTimes(PARTNER_ID, 35L, expectedNewStatus, any(), any()) }

        if (currentStatus == ReportStatus.InControl)
            verify(exactly = 1) { reportExpenditurePersistence.markAsSampledAndLock(setOf(21L)) }

        assertThat(submissionTime.captured).isAfter(ZonedDateTime.now().minusMinutes(1))
        assertThat(submissionTime.captured).isBefore(ZonedDateTime.now().plusMinutes(1))

        val expectedAction = when (expectedNewStatus) {
            ReportStatus.Submitted -> VerificationAction.ClearDeductions
            ReportStatus.InControl,
            ReportStatus.ReOpenCertified -> VerificationAction.UpdateCertified
            else -> throw IllegalStateException("not real scenario")
        }

        assertThat(slotVerificationPreparationAction.captured).isEqualTo(expectedAction)

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_REPORT_SUBMITTED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("FG01_654")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(888L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[FG01_654] [PP1] Partner report R.4 submitted [Contains sensitive data]")
        assertThat(slotRates.captured).containsExactly(
            ProjectPartnerReportExpenditureCurrencyRateChange(630L, BigDecimal.valueOf(254855L, 4), BigDecimal.valueOf(999L, 2)),
            ProjectPartnerReportExpenditureCurrencyRateChange(631L, BigDecimal.valueOf(77895L, 4), BigDecimal.valueOf(623L, 2)),
            ProjectPartnerReportExpenditureCurrencyRateChange(632L, BigDecimal.ONE, BigDecimal.valueOf(1650L, 2)),
        )
        if (shouldUpdateFinancing) {
            assertThat(expenditureCcSlot.captured).isEqualTo(expectedPersistedExpenditureCostCategory)
            assertThat(coFinSlot.captured).isEqualTo(ExpenditureCoFinancingCurrentWithReIncluded(expectedCoFinancing, expectedReIncludedCoFinancing))
            assertThat(lumpSumSlot.captured).containsExactlyEntriesOf(
                mapOf(
                    22L to ExpenditureLumpSumCurrentWithReIncluded(
                        current = BigDecimal.valueOf(485, 1),
                        currentReIncluded = BigDecimal.valueOf(485, 1),
                    ),
                )
            )
            assertThat(unitCostSlot.captured).containsExactlyEntriesOf(
                mapOf(
                    15L to ExpenditureUnitCostCurrentWithReIncluded(
                        current = BigDecimal.valueOf(165L, 1),
                        currentReIncluded = BigDecimal.ZERO,
                    ),
                )
            )
            assertThat(investmentSlot.captured).containsExactlyEntriesOf(
                mapOf(
                    10L to ExpenditureInvestmentCurrentWithReIncluded(
                        current = BigDecimal.valueOf(999L, 2),
                        currentReIncluded = BigDecimal.ZERO,
                    ),
                )
            )
        }
    }

    @Test
    fun `submit - no financial changes`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.ReOpenInControlLimited
        every { report.id } returns 36L
        every { report.lastControlReopening } returns null
        every { report.identification.coFinancing } returns coFinancing

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 36L) } returns report
        every { preSubmissionCheck.preCheck(PARTNER_ID, reportId = 36L) } returns PreConditionCheckResult(emptyList(), true)

        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, 36L) } returns emptyList()
        every { currencyPersistence.findAllByIdYearAndIdMonth(year = YEAR, month = MONTH) } returns emptyList()

        val submissionTime = slot<ZonedDateTime>()
        every { reportPersistence.updateStatusAndTimes(any(), any(), any(), any(), capture(submissionTime)) } returns mockedResult
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "5.6.0") } returns PROJECT_ID
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns mockk()

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { auditPublisher.publishEvent(ofType(PartnerReportStatusChanged::class)) } returns Unit

        val slotRates = slot<Collection<ProjectPartnerReportExpenditureCurrencyRateChange>>()
        every {
            reportExpenditureVerificationPersistence
                .updateCurrencyRatesAndPrepareVerification(
                    36L,
                    capture(slotRates),
                    VerificationAction.UpdateCertified,
                )

        } returns emptyList()

        submitReport.submit(PARTNER_ID, 36L)

        verify(exactly = 1) { reportPersistence.updateStatusAndTimes(PARTNER_ID, 36L, ReportStatus.InControl, any(), any()) }
        verify(exactly = 0) { reportExpenditurePersistence.markAsSampledAndLock(any()) }
        assertThat(submissionTime.captured).isAfter(ZonedDateTime.now().minusMinutes(1))
        assertThat(submissionTime.captured).isBefore(ZonedDateTime.now().plusMinutes(1))

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PARTNER_REPORT_SUBMITTED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("FG01_654")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(888L)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[FG01_654] [PP1] Partner report R.4 submitted")

        assertThat(slotRates.captured).isEmpty()
    }

    @Test
    fun `submit - report is not draft`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Submitted

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 36L) } returns report

        assertThrows<ReportAlreadyClosed> { submitReport.submit(PARTNER_ID, 36L) }
        verify(exactly = 0) { reportPersistence.updateStatusAndTimes(any(), any(), any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `submit - pre check failed`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Draft

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 44L) } returns report
        every { preSubmissionCheck.preCheck(PARTNER_ID, reportId = 44L) } returns PreConditionCheckResult(emptyList(), false)

        assertThrows<SubmissionNotAllowed> { submitReport.submit(PARTNER_ID, 44L) }
        verify(exactly = 0) { reportExpenditureVerificationPersistence.updateCurrencyRatesAndPrepareVerification(any(), any(), any()) }
    }

    @Test
    fun `submit - needed rates not available`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.Draft
        every { report.lastControlReopening } returns null

        every { reportPersistence.getPartnerReportById(PARTNER_ID, 40L) } returns report
        every { preSubmissionCheck.preCheck(PARTNER_ID, reportId = 40L) } returns PreConditionCheckResult(emptyList(), true)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, 40L) } returns
                listOf(expenditure1)
        every { currencyPersistence.findAllByIdYearAndIdMonth(year = YEAR, month = MONTH) } returns emptyList()

        assertThrows<CurrencyRatesMissing> { submitReport.submit(PARTNER_ID, 40L) }
        verify(exactly = 0) { reportExpenditureVerificationPersistence.updateCurrencyRatesAndPrepareVerification(any(), any(), any()) }
    }

}
