package io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.Finalized
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.InVerification
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.ProjectReportFinancialOverviewPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.getPartnerReportFinancialData.GetPartnerReportFinancialData
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class FinalizeVerificationProjectReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 21L

        private val reportSubmissionSummary = ProjectReportSubmissionSummary(
            id = 52L,
            reportNumber = 4,
            status = Finalized,
            version = "5.6.1",
            firstSubmission = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "NS-AQ01",
            projectAcronym = "acronym",
            projectId = PROJECT_ID,
        )

        private fun report(status: ProjectReportStatus): ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            every { report.id } returns 52L
            every { report.status } returns status
            return report
        }

        private const val PROJECT_REPORT_ID = 20L
        private const val REPORT_ID = 101L
        private const val PARTNER_ID = 10L
        private const val TYPOLOGY_OF_ERROR_ID = 3L
        private const val EXPENDITURE_ID = 1L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)
        private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

        private val dummyInvestmentLine = ExpenditureInvestmentBreakdownLine(
            reportInvestmentId = 845L,
            investmentId = 22L,
            investmentNumber = 1,
            workPackageNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            currentReport = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.valueOf(100),
            currentReportReIncluded = BigDecimal.ZERO,
            deactivated = false,
            previouslyValidated = BigDecimal.valueOf(7)
        )

        private val procurement = ProjectPartnerReportProcurement(
            id = 265,
            reportId = REPORT_ID,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = YESTERDAY,
            contractName = "contractName 265",
            referenceNumber = "referenceNumber 100",
            contractDate = NEXT_WEEK,
            contractType = "contractType 265",
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName 265",
            vatNumber = "vat number 265",
            comment = "comment 265",
        )

        private val dummyLineUnitCost = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = 44L,
            unitCostId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some unit cost 44 (or 945)")),
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            currentReport = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO,
            previouslyValidated = BigDecimal.valueOf(8)
        )

        private val dummyLineLumpSum = ExpenditureLumpSumBreakdownLine(
            reportLumpSumId = 36L,
            lumpSumId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
            period = 4,
            totalEligibleBudget = BigDecimal.ONE,
            previouslyReported = BigDecimal.TEN,
            previouslyPaid = BigDecimal.ONE,
            currentReport = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
            totalReportedSoFarPercentage = BigDecimal.TEN,
            remainingBudget = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.valueOf(1000),
            currentReportReIncluded = BigDecimal.valueOf(100),
            previouslyValidated = BigDecimal.valueOf(6)
        )

        private val parkingMetadata = ExpenditureParkingMetadata(
            reportOfOriginId = 70L,
            reportOfOriginNumber = 5,
            reportProjectOfOriginId = PROJECT_REPORT_ID,
            originalExpenditureNumber = 3
        )

        private val expenditureItem = ProjectPartnerReportExpenditureItem(
            id = EXPENDITURE_ID,
            number = 1,

            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,

            partnerReportId = REPORT_ID,
            partnerReportNumber = 1,

            lumpSum = dummyLineLumpSum,
            unitCost = dummyLineUnitCost,
            gdpr = false,
            costCategory = ReportBudgetCategory.EquipmentCosts,
            investment = dummyInvestmentLine,
            contract = procurement,
            internalReferenceNumber = "internal-1",
            invoiceNumber = "invoice-1",
            invoiceDate = LocalDate.of(2022, 1, 1),
            dateOfPayment = LocalDate.of(2022, 2, 1),
            description = emptySet(),
            comment = emptySet(),
            totalValueInvoice = BigDecimal.valueOf(22),
            vat = BigDecimal.valueOf(18.0),
            numberOfUnits = BigDecimal.ZERO,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.valueOf(31.2),
            currencyCode = "CZK",
            currencyConversionRate = BigDecimal.valueOf(24),
            declaredAmountAfterSubmission = BigDecimal.valueOf(1.3),
            attachment = JemsFileMetadata(500L, "file.txt", UPLOADED),

            partOfSample = false,
            partOfSampleLocked = false,
            certifiedAmount = BigDecimal.valueOf(101),
            deductedAmount = BigDecimal.valueOf(101),
            typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
            parked = true,
            verificationComment = "VERIFICATION COMM",

            parkingMetadata = parkingMetadata
        )

        private val aggregatedExpenditures =
            ProjectReportVerificationExpenditureLine(
                expenditure = expenditureItem,
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(100),
                deductedByMa = BigDecimal.valueOf(200),
                amountAfterVerification = BigDecimal.valueOf(300),
                typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
                parked = true,
                verificationComment = "VERIFICATION COMM"
            )

        val ERDF = ProgrammeFund(
            id = 1L, type = ProgrammeFundType.ERDF, selected = true,
            abbreviation = setOf(
                InputTranslation(
                    SystemLanguage.EN, "EN ERDF"
                ),
                InputTranslation(SystemLanguage.SK, "SK ERDF")
            ),
            description = setOf(
                InputTranslation(SystemLanguage.EN, "EN desc"),
                InputTranslation(SystemLanguage.SK, "SK desc")
            )
        )

        val financialData = FinancingSourceBreakdownLine(
            partnerReportId = 23L,
            partnerReportNumber = 2,
            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,
            fundsSorted = listOf(Pair(ERDF, BigDecimal.ZERO)),
            partnerContribution = BigDecimal.ZERO,
            publicContribution = BigDecimal.ZERO,
            automaticPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            total = BigDecimal.ZERO,
            split = emptyList()
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var expenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence

    @RelaxedMockK
    lateinit var projectReportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence

    @RelaxedMockK
    lateinit var getPartnerReportFinancialData: GetPartnerReportFinancialData

    @RelaxedMockK
    lateinit var projectReportFinancialOverviewPersistence: ProjectReportFinancialOverviewPersistence

    @InjectMockKs
    lateinit var interactor: FinalizeVerificationProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence)
        clearMocks(auditPublisher)
    }

    @Test
    fun finalizeVerification() {
        val reportId = 52L
        val report = report(InVerification)

        every { reportPersistence.getReportById(PROJECT_ID, reportId) } returns report
        every { expenditureVerificationPersistence.getParkedProjectReportExpenditureVerification(reportId) } returns listOf(
            aggregatedExpenditures
        )

        every {
            reportPersistence.finalizeVerificationOnReportById(
                PROJECT_ID,
                reportId
            )
        } returns reportSubmissionSummary


        every { expenditureVerificationPersistence.getProjectReportExpenditureVerification(reportId) } returns listOf(aggregatedExpenditures)
        every { projectReportCertificateCoFinancingPersistence.getAvailableFunds(reportId) } returns listOf(ERDF)
        every { getPartnerReportFinancialData.retrievePartnerReportFinancialData(reportId) } returns mockk()


        every { projectReportFinancialOverviewPersistence.storeOverviewPerFund(PROJECT_REPORT_ID, any()) } returns listOf(financialData)
        every { projectReportCertificateCoFinancingPersistence.updateAfterVerificationValues(PROJECT_ID, PROJECT_REPORT_ID, any()) } returns Unit


        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { auditPublisher.publishEvent(ofType(ProjectReportStatusChanged::class)) } returns Unit

        assertThat(interactor.finalizeVerification(PROJECT_ID, reportId)).isEqualTo(Finalized)

        verify(exactly = 1) { reportPersistence.finalizeVerificationOnReportById(PROJECT_ID, reportId) }

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_REPORT_VERIFICATION_FINALIZED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo(PROJECT_ID.toString())
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("NS-AQ01")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(reportId)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo("[NS-AQ01] Project report R.4 verification was finalised and following expenditure items were parked:  [LP1] - item [R1.1], ")
    }

    @ParameterizedTest(name = "startVerification - wrong status (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification"], mode = EnumSource.Mode.EXCLUDE)
    fun `finalizeVerification - wrong status`(reportStatus: ProjectReportStatus) {
        val reportId = 52L
        val report = report(reportStatus)

        every { reportPersistence.getReportById(PROJECT_ID, reportId) } returns report
        assertThrows<ReportVerificationNotStartedException> { interactor.finalizeVerification(PROJECT_ID, reportId) }

        verify(exactly = 0) { reportPersistence.finalizeVerificationOnReportById(any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }
}
