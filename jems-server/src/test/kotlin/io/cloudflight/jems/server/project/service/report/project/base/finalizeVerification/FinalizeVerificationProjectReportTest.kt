package io.cloudflight.jems.server.project.service.report.project.base.finalizeVerification

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO.MainFund
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.notification.handler.ProjectReportStatusChanged
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.model.regular.toCreate.PaymentRegularToCreate
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.repository.report.project.financialOverview.costCategory.ProjectReportCertificateCostCategoryPersistenceProvider
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.Finalized
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus.InVerification
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerCertificateFundSplit
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerReportFinancialData
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.ProjectReportFinancialOverviewPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.getPartnerReportFinancialData.GetPartnerReportFinancialData
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
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class FinalizeVerificationProjectReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 21L
        private val DATE_TIME_NOW = ZonedDateTime.now()

        private fun reportSubmissionSummary(reportId: Long, reportNumber: Int) = ProjectReportSubmissionSummary(
            id = reportId,
            reportNumber = reportNumber,
            status = Finalized,
            version = "5.6.1",
            firstSubmission = ZonedDateTime.now(),
            createdAt = ZonedDateTime.now(),
            projectIdentifier = "NS-AQ01",
            projectAcronym = "acronym",
            projectId = PROJECT_ID,
            periodNumber = 1
        )

        private fun report(reportId: Long, status: ProjectReportStatus, spfPartnerId: Long?): ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            every { report.id } returns reportId
            every { report.status } returns status
            every { report.projectId } returns PROJECT_ID
            every { report.spfPartnerId } returns spfPartnerId
            return report
        }

        private const val PROJECT_REPORT_ID = 20L
        private const val PARTNER_REPORT_ID = 101L
        private const val PARTNER_ID = 10L
        private const val TYPOLOGY_OF_ERROR_ID = 3L
        private const val EXPENDITURE_ID = 1L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)
        private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

        private val dummyInvestmentLine = ProjectPartnerReportInvestment(
            id = 845L,
            investmentId = 22L,
            investmentNumber = 1,
            workPackageNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
            total = BigDecimal.ONE,
            deactivated = false,
        )

        private val procurement = ProjectPartnerReportProcurement(
            id = 265,
            reportId = PARTNER_REPORT_ID,
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

        private val dummyLineUnitCost = ProjectPartnerReportUnitCost(
            id = 44L,
            unitCostProgrammeId = 945L,
            projectDefined = false,
            costPerUnit = BigDecimal.ONE,
            numberOfUnits = BigDecimal.TEN,
            total = BigDecimal.TEN,
            name = setOf(InputTranslation(SystemLanguage.EN, "some unit cost 44 (or 945)")),
            category = ReportBudgetCategory.ExternalCosts,
        )

        private val dummyLineLumpSum = ProjectPartnerReportLumpSum(
            id = 36L,
            lumpSumProgrammeId = 945L,
            fastTrack = false,
            orderNr = 7,
            period = 4,
            cost = BigDecimal.TEN,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
        )

        private val parkingMetadata = ExpenditureParkingMetadata(
            reportOfOriginId = 70L,
            reportOfOriginNumber = 5,
            reportProjectOfOriginId = PROJECT_REPORT_ID,
            originalExpenditureNumber = 3,
            parkedFromExpenditureId = EXPENDITURE_ID,
            parkedOn = DATE_TIME_NOW
        )

        private val expenditureItem = ProjectPartnerReportExpenditureItem(
            id = EXPENDITURE_ID,
            number = 1,

            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,

            partnerReportId = PARTNER_REPORT_ID,
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
                verificationComment = "VERIFICATION COMM",
                parkedOn = YESTERDAY
            )

        private val verification =
            ProjectReportVerificationExpenditureLine(
                expenditure = mockk<ProjectPartnerReportExpenditureItem> {
                    every { id } returns 724L
                    every { partnerId } returns PARTNER_ID
                    every { partnerRole } returns ProjectPartnerRole.PARTNER
                    every { partnerNumber } returns 2
                    every { partnerReportId } returns PARTNER_REPORT_ID
                    every { partnerReportNumber } returns 12
                    every { costCategory } returns ReportBudgetCategory.Multiple
                    every { lumpSum } returns dummyLineLumpSum
                    every { unitCost } returns null
                    every { investment } returns dummyInvestmentLine
                    every { declaredAmountAfterSubmission } returns BigDecimal.valueOf(-999) // not used
                    every { certifiedAmount } returns BigDecimal.valueOf(600)
                    every { parkingMetadata } returns null
                },
                partOfVerificationSample = false,
                deductedByJs = BigDecimal.valueOf(100),
                deductedByMa = BigDecimal.valueOf(200),
                amountAfterVerification = BigDecimal.valueOf(300),
                typologyOfErrorId = TYPOLOGY_OF_ERROR_ID,
                parked = true,
                verificationComment = "VERIFICATION COMM",
                parkedOn = YESTERDAY
            )

        private fun parkedExpenditure(): ProjectReportVerificationExpenditureLine {
            val expenditure = mockk<ProjectPartnerReportExpenditureItem>()
            every { expenditure.number } returns 71
            every { expenditure.partnerRole } returns ProjectPartnerRole.PARTNER
            every { expenditure.partnerNumber } returns 4
            every { expenditure.partnerReportNumber } returns 48

            val parked = mockk<ProjectReportVerificationExpenditureLine>()
            every { parked.expenditure } returns expenditure
            return parked
        }

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

        fun spfContribution() = FinancingSourceBreakdownLine(
            partnerReportId = null,
            partnerReportNumber = null,
            spfLine = true,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = listOf(
                Pair(ERDF, BigDecimal.valueOf(140L)),
            ),
            partnerContribution = BigDecimal.valueOf(60L),
            publicContribution = BigDecimal.valueOf(18L),
            automaticPublicContribution = BigDecimal.valueOf(20L),
            privateContribution = BigDecimal.valueOf(22L),
            total = BigDecimal.valueOf(200L),
            split = emptyList(),
        )

        val options = mockk<PartnerReportFinancialData> {
            every { coFinancingFromAF } returns listOf(
                ProjectPartnerCoFinancing(PartnerContribution, null, BigDecimal.valueOf(25)),
                ProjectPartnerCoFinancing(MainFund, ERDF, BigDecimal.valueOf(75)),
            )
            every { contributionsFromAF } returns ProjectPartnerReportContributionOverview(
                public = mockk { every { amount } returns BigDecimal.valueOf(65L) },
                automaticPublic = mockk { every { amount } returns BigDecimal.valueOf(84L) },
                private = mockk { every { amount } returns BigDecimal.valueOf(101L) },
                total = mockk(),
            )
            every { totalEligibleBudgetFromAFWithoutSpf } returns BigDecimal.valueOf(1000L)
            every { flatRatesFromAF } returns ProjectPartnerBudgetOptions(-1L, 10, null, 12, 30, null)
        }

        val afterSaveSplits = listOf(
            PartnerCertificateFundSplit(
                partnerReportId = PARTNER_REPORT_ID,
                partnerId = PARTNER_ID,
                fundId = ERDF.id,
                value = BigDecimal.valueOf(225L),
                defaultPartnerContribution = BigDecimal.valueOf(75L),
                defaultOfWhichPublic = BigDecimal.valueOf(1950L, 2),
                defaultOfWhichAutoPublic = BigDecimal.valueOf(2520L, 2),
                defaultOfWhichPrivate = BigDecimal.valueOf(3030L, 2),
                total = BigDecimal.valueOf(300L),
            ),
            PartnerCertificateFundSplit(
                partnerReportId = null,
                partnerId = PARTNER_ID,
                fundId = ERDF.id,
                value = BigDecimal.valueOf(140L),
                defaultPartnerContribution = BigDecimal.valueOf(60L),
                defaultOfWhichPublic = BigDecimal.valueOf(18L),
                defaultOfWhichAutoPublic = BigDecimal.valueOf(20L),
                defaultOfWhichPrivate = BigDecimal.valueOf(22L),
                total = BigDecimal.valueOf(200L),
            ),
        )

        val reportCertificatesOverviewPerFund = listOf(PartnerCertificateFundSplit(
                    partnerReportId = 106,
                    partnerId = 92,
                    fundId = 1,
                    value = BigDecimal(800.00),
                    total = BigDecimal(1000.00),
                    defaultOfWhichAutoPublic = BigDecimal.ZERO,
                    defaultOfWhichPrivate = BigDecimal.ZERO,
                    defaultOfWhichPublic = BigDecimal.ZERO,
                    defaultPartnerContribution = BigDecimal.ZERO
                ), PartnerCertificateFundSplit(
                    partnerReportId = 107,
                    partnerId = 91,
                    fundId = 1,
                    value = BigDecimal(400.00),
                    total = BigDecimal(597.01),
                    defaultOfWhichAutoPublic = BigDecimal.ZERO,
                    defaultOfWhichPrivate = BigDecimal.ZERO,
                    defaultOfWhichPublic = BigDecimal.ZERO,
                    defaultPartnerContribution = BigDecimal.ZERO
                ), PartnerCertificateFundSplit(
                    partnerReportId = 108,
                    partnerId = 91,
                    fundId = 1,
                    value = BigDecimal(400.00),
                    total = BigDecimal(597.01),
                    defaultOfWhichAutoPublic = BigDecimal.ZERO,
                    defaultOfWhichPrivate = BigDecimal.ZERO,
                    defaultOfWhichPublic = BigDecimal.ZERO,
                    defaultPartnerContribution = BigDecimal.ZERO
                ),
                PartnerCertificateFundSplit(
                    partnerReportId = 107,
                    partnerId = 91,
                    fundId = 4,
                    value = BigDecimal(180.00),
                    total = BigDecimal(268.66),
                    defaultOfWhichAutoPublic = BigDecimal.ZERO,
                    defaultOfWhichPrivate = BigDecimal.ZERO,
                    defaultOfWhichPublic = BigDecimal.ZERO,
                    defaultPartnerContribution = BigDecimal.ZERO
                ),
                PartnerCertificateFundSplit(
                    partnerReportId = 108,
                    partnerId = 91,
                    fundId = 4,
                    value = BigDecimal(180.00),
                    total = BigDecimal(268.66),
                    defaultOfWhichAutoPublic = BigDecimal.ZERO,
                    defaultOfWhichPrivate = BigDecimal.ZERO,
                    defaultOfWhichPublic = BigDecimal.ZERO,
                    defaultPartnerContribution = BigDecimal.ZERO
                ),
                PartnerCertificateFundSplit(
                    partnerReportId = 107,
                    partnerId = 91,
                    fundId = 5,
                    value = BigDecimal(90.00),
                    total = BigDecimal(134.33),
                    defaultOfWhichAutoPublic = BigDecimal.ZERO,
                    defaultOfWhichPrivate = BigDecimal.ZERO,
                    defaultOfWhichPublic = BigDecimal.ZERO,
                    defaultPartnerContribution = BigDecimal.ZERO
                ),
                PartnerCertificateFundSplit(
                    partnerReportId = 108,
                    partnerId = 91,
                    fundId = 5,
                    value = BigDecimal(90.00),
                    total = BigDecimal(134.33),
                    defaultOfWhichAutoPublic = BigDecimal.ZERO,
                    defaultOfWhichPrivate = BigDecimal.ZERO,
                    defaultOfWhichPublic = BigDecimal.ZERO,
                    defaultPartnerContribution = BigDecimal.ZERO
                )
            )

        private val expectedFinanceFund = FinancingSourceBreakdownLine(
            partnerReportId = PARTNER_REPORT_ID,
            partnerReportNumber = 12,
            spfLine = false,
            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.PARTNER,
            partnerNumber = 2,
            fundsSorted = listOf(
                Pair(ERDF, BigDecimal.valueOf(225_00L, 2))
            ),
            partnerContribution = BigDecimal.valueOf(7500L, 2),
            publicContribution = BigDecimal.valueOf(1950L, 2),
            automaticPublicContribution = BigDecimal.valueOf(2520L, 2),
            privateContribution = BigDecimal.valueOf(3030L, 2),
            total = BigDecimal.valueOf(30000L, 2),
            split = listOf(
                FinancingSourceBreakdownSplitLine(
                    fundId = 1L,
                    value = BigDecimal.valueOf(225_00L, 2),
                    partnerContribution = BigDecimal.valueOf(7500L, 2),
                    publicContribution = BigDecimal.valueOf(1950L, 2),
                    automaticPublicContribution = BigDecimal.valueOf(2520L, 2),
                    privateContribution = BigDecimal.valueOf(3030L, 2),
                    total = BigDecimal.valueOf(30000L, 2),
                ),
            ),
        )

        private val expectedFinanceSpf = FinancingSourceBreakdownLine(
            partnerReportId = null,
            partnerReportNumber = null,
            spfLine = true,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = listOf(
                Pair(ERDF, BigDecimal.valueOf(140L))
            ),
            partnerContribution = BigDecimal.valueOf(60L),
            publicContribution = BigDecimal.valueOf(18L),
            automaticPublicContribution = BigDecimal.valueOf(20L),
            privateContribution = BigDecimal.valueOf(22L),
            total = BigDecimal.valueOf(200L),
            split = listOf(
                FinancingSourceBreakdownSplitLine(
                    fundId = 1L,
                    value = BigDecimal.valueOf(140L),
                    partnerContribution = BigDecimal.valueOf(6000L, 2),
                    publicContribution = BigDecimal.valueOf(1800L, 2),
                    automaticPublicContribution = BigDecimal.valueOf(2000L, 2),
                    privateContribution = BigDecimal.valueOf(2200L, 2),
                    total = BigDecimal.valueOf(20000L, 2),
                ),
            ),
        )

        private val expectedCoFin = ReportCertificateCoFinancingColumn(
            funds = mapOf(1L to BigDecimal.valueOf(36500L, 2), null to BigDecimal.valueOf(13500L, 2)),
            partnerContribution = BigDecimal.valueOf(13500L, 2),
            publicContribution = BigDecimal.valueOf(3750L, 2),
            automaticPublicContribution = BigDecimal.valueOf(4520L, 2),
            privateContribution = BigDecimal.valueOf(5230L, 2),
            sum = BigDecimal.valueOf(50000L, 2),
        )

        private val expectedCostCategory = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(0L, 2),
            office = BigDecimal.valueOf(0L, 2),
            travel = BigDecimal.valueOf(0L, 2),
            external = BigDecimal.valueOf(0L),
            equipment = BigDecimal.valueOf(0L),
            infrastructure = BigDecimal.valueOf(0L),
            other = BigDecimal.valueOf(0L),
            lumpSum = BigDecimal.valueOf(300L),
            unitCost = BigDecimal.valueOf(0L),
            spfCost = BigDecimal.valueOf(200L),
            sum = BigDecimal.valueOf(500_00L, 2),
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    private lateinit var expenditureVerificationPersistence: ProjectReportVerificationExpenditurePersistence

    @MockK
    private lateinit var projectReportFinancialOverviewPersistence: ProjectReportFinancialOverviewPersistence

    @MockK
    private lateinit var getPartnerReportFinancialData: GetPartnerReportFinancialData

    @MockK
    private lateinit var paymentRegularPersistence: PaymentPersistence

    @MockK
    private lateinit var partnerReportCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence

    @MockK
    private lateinit var reportCertificateCostCategoryPersistence: ProjectReportCertificateCostCategoryPersistenceProvider

    @MockK
    private lateinit var reportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence

    @MockK
    private lateinit var reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence

    @MockK
    private lateinit var reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence

    @MockK
    private lateinit var reportInvestmentPersistence: ProjectReportCertificateInvestmentPersistence

    @MockK
    private lateinit var reportSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence

    @MockK
    private lateinit var  reportContributionPersistence: ProjectPartnerReportContributionPersistence
    @MockK
    private lateinit var  reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence
    @MockK
    private lateinit var  partnerReportPersistence: ProjectPartnerReportPersistence
    @MockK
    private lateinit var  reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence
    @MockK
    private lateinit var  reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence
    @MockK
    private lateinit var  partnerReportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence


    @InjectMockKs
    lateinit var interactor: FinalizeVerificationProjectReport

    @BeforeEach
    fun reset() {
        clearMocks(reportPersistence, auditPublisher, expenditureVerificationPersistence, paymentRegularPersistence,
            projectReportFinancialOverviewPersistence,reportContributionPersistence, reportExpenditureCostCategoryPersistence,
            partnerReportPersistence, reportLumpSumPersistence,reportUnitCostPersistence, getPartnerReportFinancialData )
    }

    @ParameterizedTest(name = "finalizeVerification (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification", "ReOpenFinalized"])
    fun finalizeVerification(status: ProjectReportStatus) {
        val reportId = 52L + status.ordinal
        val spfPartnerId = if (status == InVerification) 666L else null
        val report = report(reportId, status, spfPartnerId = spfPartnerId)
        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report

        // financialData
        every { expenditureVerificationPersistence.getProjectReportExpenditureVerification(reportId) } returns listOf(verification)
        every { partnerReportCoFinancingPersistence.getAvailableFunds(PARTNER_REPORT_ID) } returns listOf(ERDF)
        every { getPartnerReportFinancialData.retrievePartnerReportFinancialData(PARTNER_REPORT_ID) } returns options
        every { reportSpfClaimPersistence.getCurrentSpfContributionSplit(reportId) } returns spfContribution()

        val financeToStore = slot<List<FinancingSourceBreakdownLine>>()
        every {
            projectReportFinancialOverviewPersistence.storeOverviewPerFund(reportId, capture(financeToStore), spfPartnerId)
        } returns afterSaveSplits

        val coFinToStore = slot<ReportCertificateCoFinancingColumn>()
        every { reportCertificateCoFinancingPersistence.updateAfterVerificationValues(PROJECT_ID, reportId, capture(coFinToStore)) } answers { }
        val costCategoryToStore = slot<BudgetCostsCalculationResultFull>()
        every { reportCertificateCostCategoryPersistence.updateAfterVerification(PROJECT_ID, reportId, capture(costCategoryToStore)) } answers { }

        every { reportCertificateLumpSumPersistence.updateCurrentlyVerifiedValues(PROJECT_ID, reportId, any()) } returns Unit
        every { reportCertificateUnitCostPersistence.updateCurrentlyVerifiedValues(PROJECT_ID, reportId, any()) } returns Unit
        every { reportInvestmentPersistence.updateCurrentlyVerifiedValues(PROJECT_ID, reportId, any()) } returns Unit

        val slotPayments = slot<Map<Long, PaymentRegularToCreate>>()
        every { paymentRegularPersistence.saveRegularPayments(reportId, capture(slotPayments)) } returns Unit

        every { expenditureVerificationPersistence.getParkedProjectReportExpenditureVerification(reportId) } returns listOf(parkedExpenditure())


        val partnerReportMock = mockk<ProjectPartnerReport>()
        every { partnerReportMock.identification.coFinancing } returns listOf(
            ProjectPartnerCoFinancing(PartnerContribution, null, BigDecimal.valueOf(25)),
            ProjectPartnerCoFinancing(MainFund, ERDF, BigDecimal.valueOf(75)),
        )
        every { partnerReportPersistence.getPartnerReportById(PARTNER_ID, PARTNER_REPORT_ID) } returns partnerReportMock

        val costCategoriesMock = mockk<ReportExpenditureCostCategory>()
        every { costCategoriesMock.options } returns options.flatRatesFromAF
        every { costCategoriesMock.totalsFromAF.sum } returns BigDecimal.valueOf(1000)
        every { costCategoriesMock.totalsFromAF.spfCost } returns BigDecimal.valueOf(200)
        every { costCategoriesMock.totalBudgetWithoutSpf() } returns BigDecimal.valueOf(800)
        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, PARTNER_REPORT_ID) } returns costCategoriesMock

        val contributionsMock = mockk<ProjectPartnerReportEntityContribution>()
        every { contributionsMock.legalStatus } returns ProjectPartnerContributionStatus.Public
        every { contributionsMock.amount } returns BigDecimal.valueOf(300)
        every { contributionsMock.previouslyReported } returns BigDecimal.ZERO
        every { contributionsMock.currentlyReported } returns BigDecimal.ZERO

        every {  reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, PARTNER_REPORT_ID) } returns listOf(contributionsMock)

        val coFinancingParkedValues = slot<ReportExpenditureCoFinancingColumn>()
        every {
            partnerReportCoFinancingPersistence.updateAfterVerificationParkedValues(
                PARTNER_ID,
                PARTNER_REPORT_ID,
                capture(coFinancingParkedValues)
            )
        } returns Unit

        val costCategoriesParkedValues = slot< BudgetCostsCalculationResultFull>()
        every {
            reportExpenditureCostCategoryPersistence.updateAfterVerificationParkedValues(
                PARTNER_ID,
                PARTNER_REPORT_ID,
                capture(costCategoriesParkedValues)
            )
        } returns Unit

        val lumpSumParkedValues = slot<Map<Long, BigDecimal>>()
        every {
            reportLumpSumPersistence.updateAfterVerificationParkedValues(
                PARTNER_ID,
                PARTNER_REPORT_ID,
                capture(lumpSumParkedValues)
            )
        } returns Unit

        val unitCostsParkedValues = slot<Map<Long, BigDecimal>>()
        every {
            reportUnitCostPersistence.updateAfterVerificationParkedValues(
                PARTNER_ID,
                PARTNER_REPORT_ID,
                capture(unitCostsParkedValues)
            )
        } returns Unit

        val investmentsParkedValues = slot<Map<Long, BigDecimal>>()
        every {
            partnerReportInvestmentPersistence.updateAfterVerificationParkedValues(
                PARTNER_ID,
                PARTNER_REPORT_ID,
                capture(investmentsParkedValues)
            )
        } returns Unit

        val slotTime = slot<ZonedDateTime>()
        every {
            reportPersistence.finalizeVerificationOnReportById(PROJECT_ID, reportId, capture(slotTime))
        } returns reportSubmissionSummary(reportId, 12)

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { auditPublisher.publishEvent(ofType(ProjectReportStatusChanged::class)) } returns Unit

        assertThat(interactor.finalizeVerification(reportId)).isEqualTo(Finalized)

        assertThat(financeToStore.captured).containsExactly(expectedFinanceFund, expectedFinanceSpf)
        assertThat(coFinToStore.captured).isEqualTo(expectedCoFin)
        assertThat(costCategoryToStore.captured).isEqualTo(expectedCostCategory)
        assertThat(slotPayments.captured).containsExactlyEntriesOf(
            mapOf(
                1L to PaymentRegularToCreate(
                    PROJECT_ID,
                    partnerPayments = listOf(
                        PaymentPartnerToCreate(PARTNER_ID, PARTNER_REPORT_ID, BigDecimal.valueOf(225L)),
                        PaymentPartnerToCreate(PARTNER_ID, null, BigDecimal.valueOf(140L)),
                    ),
                    amountApprovedPerFund = BigDecimal.valueOf(365L),
                    defaultPartnerContribution = BigDecimal.valueOf(135L),
                    defaultOfWhichPublic = BigDecimal.valueOf(3750L, 2),
                    defaultOfWhichAutoPublic = BigDecimal.valueOf(4520L, 2),
                    defaultOfWhichPrivate = BigDecimal.valueOf(5230L, 2),
                    defaultTotalEligibleWithoutSco = BigDecimal.valueOf(500),
                    defaultFundAmountUnionContribution = BigDecimal.ZERO,
                    defaultFundAmountPublicContribution = BigDecimal.valueOf(365),
                ),
            ),
        )

        verify(exactly = 1) { reportPersistence.finalizeVerificationOnReportById(PROJECT_ID, reportId, any()) }
        assertThat(slotTime.captured).isAfter(ZonedDateTime.now().minusMinutes(1))
        assertThat(slotTime.captured).isBefore(ZonedDateTime.now().plusMinutes(1))

        assertThat(coFinancingParkedValues.captured).isEqualTo(
            ReportExpenditureCoFinancingColumn(
                funds = mapOf(  1L to BigDecimal.valueOf(45000, 2),
                                null to BigDecimal.valueOf(15000, 2)
                ),
                partnerContribution = BigDecimal.valueOf(15000, 2),
                publicContribution = BigDecimal.valueOf(22500, 2),
                automaticPublicContribution = BigDecimal.valueOf(0, 2),
                privateContribution = BigDecimal.valueOf(0, 2),
                sum = BigDecimal.valueOf(60000, 2)
            )
        )
        assertThat(costCategoriesParkedValues.captured.lumpSum).isEqualTo(BigDecimal.valueOf(600))
        assertThat(investmentsParkedValues.captured).isEqualTo(mapOf(22L to BigDecimal.valueOf(600)))

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_REPORT_VERIFICATION_FINALIZED)
        assertThat(auditSlot.captured.auditCandidate.project?.id).isEqualTo("21")
        assertThat(auditSlot.captured.auditCandidate.project?.customIdentifier).isEqualTo("NS-AQ01")
        assertThat(auditSlot.captured.auditCandidate.project?.name).isEqualTo("acronym")
        assertThat(auditSlot.captured.auditCandidate.entityRelatedId).isEqualTo(reportId)
        assertThat(auditSlot.captured.auditCandidate.description)
            .isEqualTo("[NS-AQ01] Project report R.12 verification was finalised and following expenditure items were parked:  [PP4] - item [R48.71]")
    }

    @ParameterizedTest(name = "startVerification - wrong status (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["InVerification", "ReOpenFinalized"], mode = EnumSource.Mode.EXCLUDE)
    fun `finalizeVerification - wrong status`(reportStatus: ProjectReportStatus) {
        val reportId = 40L
        val report = report(reportId, reportStatus, null)

        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report
        assertThrows<ReportVerificationNotStartedException> { interactor.finalizeVerification(reportId) }

        verify(exactly = 0) { reportPersistence.finalizeVerificationOnReportById(any(), any(), any()) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `Regular payments data is generated correctly`() {
        val reportId = 52L
        val report = report(reportId, InVerification, null)

        val expectedPaymentsToSave = mapOf(
            1L to PaymentRegularToCreate(
                projectId = PROJECT_ID,
                partnerPayments = listOf(
                    PaymentPartnerToCreate(
                        partnerId = 92,
                        partnerReportId = 106,
                        amountApprovedPerPartner = BigDecimal(800.00)
                    ), PaymentPartnerToCreate(
                        partnerId = 91,
                        partnerReportId = 107,
                        amountApprovedPerPartner = BigDecimal(400.00)
                    ), PaymentPartnerToCreate(
                        partnerId = 91,
                        partnerReportId = 108,
                        amountApprovedPerPartner = BigDecimal(400.00)
                    )
                ),
                amountApprovedPerFund = BigDecimal(1600.00),
                defaultOfWhichAutoPublic = BigDecimal.ZERO,
                defaultOfWhichPrivate = BigDecimal.ZERO,
                defaultOfWhichPublic = BigDecimal.ZERO,
                defaultPartnerContribution = BigDecimal.ZERO,
                defaultTotalEligibleWithoutSco = BigDecimal(1600),
                defaultFundAmountUnionContribution = BigDecimal.ZERO,
                defaultFundAmountPublicContribution = BigDecimal(1600),

            ), 4L to PaymentRegularToCreate(
                projectId = PROJECT_ID,
                partnerPayments = listOf(
                    PaymentPartnerToCreate(
                        partnerId = 91,
                        partnerReportId = 107,
                        amountApprovedPerPartner = BigDecimal(180.00)
                    ), PaymentPartnerToCreate(
                        partnerId = 91,
                        partnerReportId = 108,
                        amountApprovedPerPartner = BigDecimal(180.00)
                    )
                ),
                amountApprovedPerFund = BigDecimal(360.00),
                defaultOfWhichAutoPublic = BigDecimal.ZERO,
                defaultOfWhichPrivate = BigDecimal.ZERO,
                defaultOfWhichPublic = BigDecimal.ZERO,
                defaultPartnerContribution = BigDecimal.ZERO,
                defaultTotalEligibleWithoutSco = BigDecimal(360),
                defaultFundAmountUnionContribution = BigDecimal.ZERO,
                defaultFundAmountPublicContribution = BigDecimal(360),
            ), 5L to PaymentRegularToCreate(
                projectId = PROJECT_ID,
                partnerPayments = listOf(
                    PaymentPartnerToCreate(
                        partnerId = 91,
                        partnerReportId = 107,
                        amountApprovedPerPartner = BigDecimal(90.00)
                    ), PaymentPartnerToCreate(
                        partnerId = 91,
                        partnerReportId = 108,
                        amountApprovedPerPartner = BigDecimal(90.00)
                    )
                ),
                amountApprovedPerFund = BigDecimal(180.00),
                defaultOfWhichAutoPublic = BigDecimal.ZERO,
                defaultOfWhichPrivate = BigDecimal.ZERO,
                defaultOfWhichPublic = BigDecimal.ZERO,
                defaultPartnerContribution = BigDecimal.ZERO,
                defaultTotalEligibleWithoutSco = BigDecimal(180.00),
                defaultFundAmountUnionContribution = BigDecimal.ZERO,
                defaultFundAmountPublicContribution = BigDecimal(180.00),
            )
        )

        every { reportPersistence.getReportByIdUnSecured(reportId) } returns report
        every { expenditureVerificationPersistence.getParkedProjectReportExpenditureVerification(reportId) } returns listOf(
            aggregatedExpenditures
        )

        val finalReport = reportSubmissionSummary(reportId, 4)
        every {
            reportPersistence.finalizeVerificationOnReportById(PROJECT_ID, reportId, any())
        } returns finalReport


        every { reportCertificateCoFinancingPersistence.getAvailableFunds(reportId) } returns listOf(ERDF)
        every { expenditureVerificationPersistence.getProjectReportExpenditureVerification(reportId) } returns listOf(aggregatedExpenditures)


        every { getPartnerReportFinancialData.retrievePartnerReportFinancialData(any()) } returns mockk {
            every { coFinancingFromAF } returns listOf(
                ProjectPartnerCoFinancing(PartnerContribution, null, BigDecimal.valueOf(25)),
                ProjectPartnerCoFinancing(MainFund, ERDF, BigDecimal.valueOf(75)),
            )
            every { contributionsFromAF } returns ProjectPartnerReportContributionOverview(
                public = mockk { every { amount } returns BigDecimal.valueOf(65L) },
                automaticPublic = mockk { every { amount } returns BigDecimal.valueOf(84L) },
                private = mockk { every { amount } returns BigDecimal.valueOf(101L) },
                total = mockk(),
            )
            every { totalEligibleBudgetFromAFWithoutSpf } returns BigDecimal.valueOf(1000L)
            every { flatRatesFromAF } returns ProjectPartnerBudgetOptions(-1L, 10, null, 12, 30, null)
        }
        every { partnerReportCoFinancingPersistence.getAvailableFunds(any()) } returns listOf(ERDF)

        val partnerReportMock = mockk<ProjectPartnerReport>()
        every { partnerReportMock.identification.coFinancing } returns listOf(
            ProjectPartnerCoFinancing(PartnerContribution, null, BigDecimal.valueOf(25)),
            ProjectPartnerCoFinancing(MainFund, ERDF, BigDecimal.valueOf(75)),
        )
        every { partnerReportPersistence.getPartnerReportById(any(), any()) } returns partnerReportMock

        val costCategoriesMock = mockk<ReportExpenditureCostCategory>()
        every { costCategoriesMock.options } returns options.flatRatesFromAF
        every { costCategoriesMock.totalsFromAF.sum } returns BigDecimal.valueOf(1000)
        every { costCategoriesMock.totalsFromAF.spfCost } returns BigDecimal.valueOf(200)
        every { costCategoriesMock.totalBudgetWithoutSpf() } returns BigDecimal.valueOf(800)
        every { reportExpenditureCostCategoryPersistence.getCostCategories(any(), any()) } returns costCategoriesMock


        val contributionsMock = mockk<ProjectPartnerReportEntityContribution>()
        every { contributionsMock.legalStatus } returns ProjectPartnerContributionStatus.Public
        every { contributionsMock.amount } returns BigDecimal.valueOf(300)
        every { contributionsMock.previouslyReported } returns BigDecimal.ZERO
        every { contributionsMock.currentlyReported } returns BigDecimal.ZERO

        every {  reportContributionPersistence.getPartnerReportContribution(any(), any()) } returns listOf(contributionsMock)



        every {
            partnerReportCoFinancingPersistence.updateAfterVerificationParkedValues(any(), any(), any())
        } returns Unit


        every {
            reportExpenditureCostCategoryPersistence.updateAfterVerificationParkedValues(any(), any(), any())
        } returns Unit


        every {
            reportLumpSumPersistence.updateAfterVerificationParkedValues(any(), any(), any())
        } returns Unit


        every {
            reportUnitCostPersistence.updateAfterVerificationParkedValues(any(), any(), any())
        } returns Unit


        every { partnerReportInvestmentPersistence.updateAfterVerificationParkedValues(any(), any(), any())
        } returns Unit

        every { reportSpfClaimPersistence.getCurrentSpfContributionSplit(reportId) } returns null

        every { projectReportFinancialOverviewPersistence.storeOverviewPerFund(reportId, any(), null) } returns reportCertificatesOverviewPerFund
        every { reportCertificateCoFinancingPersistence.updateAfterVerificationValues(PROJECT_ID, reportId, any()) } returns Unit
        every { reportCertificateCostCategoryPersistence.updateAfterVerification(PROJECT_ID, reportId, any()) } returns Unit
        every { reportCertificateLumpSumPersistence.updateCurrentlyVerifiedValues(PROJECT_ID, reportId, any()) } returns Unit
        every { reportCertificateUnitCostPersistence.updateCurrentlyVerifiedValues(PROJECT_ID, reportId, any()) } returns Unit
        every { reportInvestmentPersistence.updateCurrentlyVerifiedValues(PROJECT_ID, reportId, any()) } returns Unit

        val slotAudit = slot<ProjectReportStatusChanged>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } answers { }
        val slotStatusChanged = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotStatusChanged)) } answers { }

        val paymentsToSaveSlot = slot<Map<Long, PaymentRegularToCreate>>()
        every { paymentRegularPersistence.saveRegularPayments(reportId, capture(paymentsToSaveSlot)) } returns Unit

        assertThat(interactor.finalizeVerification(reportId)).isEqualTo(Finalized)
        assertThat(paymentsToSaveSlot.captured).isEqualTo(expectedPaymentsToSave)

        assertThat(slotAudit.captured.projectReportSummary).isEqualTo(finalReport)
        assertThat(slotAudit.captured.previousReportStatus).isEqualTo(InVerification)
        assertThat(slotStatusChanged.captured.auditCandidate).isEqualTo(
            AuditCandidate(AuditAction.PROJECT_REPORT_VERIFICATION_FINALIZED,
                AuditProject("21", "NS-AQ01", "acronym"), 52L,
                "[NS-AQ01] Project report R.4 verification was finalised and following expenditure items were parked:  [LP1] - item [R1.1]")
        )
    }
}
