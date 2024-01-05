package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.ProjectPartnerReportContributionRow
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.PartnerReportFinancialData
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.ProjectReportFinancialOverviewPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.getPartnerReportFinancialData.GetPartnerReportFinancialData
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.cloudflight.jems.server.utils.IPA_III_FUND
import io.cloudflight.jems.server.utils.NDCI_FUND
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime


class GetProjectReportFinancingSourceBreakdownCalculatorTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 13L
        const val REPORT_ID = 14L
        const val PARTNER_REPORT_ID = 38L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)

        val finalizedExpenditureLine = FinancingSourceBreakdownLine(
            partnerReportId = PARTNER_REPORT_ID,
            partnerReportNumber = 2,
            spfLine = false,
            partnerId = 3,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 4,
            fundsSorted = listOf(
                Pair(ERDF_FUND, mockk()),
                Pair(NDCI_FUND, mockk()),
            ),
            partnerContribution = mockk(),
            publicContribution = mockk(),
            automaticPublicContribution = mockk(),
            privateContribution = mockk(),
            total = mockk(),
            split = mockk(),
        )

        val spfExpenditureLine = FinancingSourceBreakdownLine(
            partnerReportId = null,
            partnerReportNumber = null,
            spfLine = true,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = listOf(
                Pair(IPA_III_FUND, mockk()),
                Pair(NDCI_FUND, mockk()),
            ),
            partnerContribution = mockk(),
            publicContribution = mockk(),
            automaticPublicContribution = mockk(),
            privateContribution = mockk(),
            total = mockk(),
            split = mockk(),
        )

        private val finalizedCoFinancingCurrentVerified = ReportCertificateCoFinancingColumn(
            funds = mapOf(
                Pair(ERDF_FUND.id, 74999.97.toScaledBigDecimal()),
                Pair(IPA_III_FUND.id, 6249.99.toScaledBigDecimal()),
                Pair(NDCI_FUND.id, 24999.99.toScaledBigDecimal()),
            ),
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.21.toScaledBigDecimal(),
            automaticPublicContribution = 5938.23.toScaledBigDecimal(),
            privateContribution = 1199.51.toScaledBigDecimal(),
            sum = 124999.96.toScaledBigDecimal(),
        )

        private val inVerificationExpenditure = ProjectReportVerificationExpenditureLine(
            expenditure = ProjectPartnerReportExpenditureItem(
                id = 123L,
                number = 1,

                partnerId = 3L,
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerNumber = 4,
                partnerReportId = PARTNER_REPORT_ID,
                partnerReportNumber = 2,
                lumpSum = mockk(),
                unitCost = mockk(),
                gdpr = false,
                costCategory = ReportBudgetCategory.EquipmentCosts,
                investment = mockk(),
                contract = mockk(),
                internalReferenceNumber = "internal-1",
                invoiceNumber = "invoice-1",
                invoiceDate = mockk(),
                dateOfPayment = mockk(),
                description = emptySet(),
                comment = emptySet(),
                totalValueInvoice = mockk(),
                vat = mockk(),
                numberOfUnits = mockk(),
                pricePerUnit = mockk(),
                declaredAmount = mockk(),
                currencyCode = "mockk",
                currencyConversionRate = mockk(),
                declaredAmountAfterSubmission = mockk(),
                attachment = null,

                partOfSample = false,
                partOfSampleLocked = false,
                certifiedAmount = mockk(),
                deductedAmount = mockk(),
                typologyOfErrorId = null,
                parked = false,
                verificationComment = null,

                parkingMetadata = null
            ),
            partOfVerificationSample = false,
            deductedByJs = mockk(),
            deductedByMa = mockk(),
            amountAfterVerification = BigDecimal.valueOf(6500L),
            typologyOfErrorId = null,
            parked = true,
            verificationComment = "VERIFICATION COMM",
            parkedOn = YESTERDAY,
        )

        private val inVerificationPartnerReportFinancialData = PartnerReportFinancialData(
            coFinancingFromAF = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ERDF_FUND,
                    percentage = BigDecimal.valueOf(60L),
                ),
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = IPA_III_FUND,
                    percentage = BigDecimal.valueOf(5L),
                ),
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                    fund = null,
                    percentage = BigDecimal.valueOf(35L),
                ),
            ),
            contributionsFromAF = ProjectPartnerReportContributionOverview(
                public = ProjectPartnerReportContributionRow(
                    amount = BigDecimal.valueOf(15_700L),
                    previouslyReported = mockk(),
                    currentlyReported = mockk(),
                    totalReportedSoFar = mockk(),
                ),
                automaticPublic = ProjectPartnerReportContributionRow(
                    amount = BigDecimal.valueOf(18_050L),
                    previouslyReported = mockk(),
                    currentlyReported = mockk(),
                    totalReportedSoFar = mockk(),
                ),
                private = ProjectPartnerReportContributionRow(
                    amount = BigDecimal.valueOf(10_000L),
                    previouslyReported = mockk(),
                    currentlyReported = mockk(),
                    totalReportedSoFar = mockk(),
                ),
                total = ProjectPartnerReportContributionRow(
                    amount = BigDecimal.valueOf(43_750L),
                    previouslyReported = mockk(),
                    currentlyReported = mockk(),
                    totalReportedSoFar = mockk(),
                ),
            ),
            totalEligibleBudgetFromAFWithoutSpf = BigDecimal.valueOf(125_000L),
            flatRatesFromAF = ProjectPartnerBudgetOptions(
                partnerId = 3L,
                officeAndAdministrationOnStaffCostsFlatRate = null,
                officeAndAdministrationOnDirectCostsFlatRate = null,
                travelAndAccommodationOnStaffCostsFlatRate = null,
                staffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
            ),
        )

        private val inVerificationSpf = FinancingSourceBreakdownLine(
            partnerReportId = null,
            partnerReportNumber = null,
            spfLine = true,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = listOf(
                ERDF_FUND to BigDecimal.valueOf(1),
                NDCI_FUND to BigDecimal.valueOf(2),
            ),
            partnerContribution = BigDecimal.valueOf(6),
            publicContribution = BigDecimal.valueOf(1),
            automaticPublicContribution = BigDecimal.valueOf(2),
            privateContribution = BigDecimal.valueOf(3),
            total = BigDecimal.valueOf(9),
            split = emptyList(),
        )

        private val breakdownFinalized = FinancingSourceBreakdown(
            sources = listOf(finalizedExpenditureLine, spfExpenditureLine),
            total = FinancingSourceBreakdownLine(
                partnerReportId = null,
                partnerReportNumber = null,
                spfLine = false,
                partnerId = null,
                partnerRole = null,
                partnerNumber = null,
                fundsSorted = listOf(
                    ERDF_FUND to 74999.97.toScaledBigDecimal(),
                    NDCI_FUND to 24999.99.toScaledBigDecimal(),
                    IPA_III_FUND to 6249.99.toScaledBigDecimal(),
                ),
                partnerContribution = 18750.01.toScaledBigDecimal(),
                publicContribution = 11612.21.toScaledBigDecimal(),
                automaticPublicContribution = 5938.23.toScaledBigDecimal(),
                privateContribution = 1199.51.toScaledBigDecimal(),
                total = 124999.96.toScaledBigDecimal(),
                split = emptyList(),
            )
        )

        private val breakdownInVerification = FinancingSourceBreakdown(
            sources = listOf(
                FinancingSourceBreakdownLine(
                    partnerReportId = 38L,
                    partnerReportNumber = 2,
                    spfLine = false,
                    partnerId = 3L,
                    partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                    partnerNumber = 4,
                    fundsSorted = listOf(
                        Pair(ERDF_FUND, BigDecimal.valueOf(3900_00L, 2)),
                        Pair(IPA_III_FUND, BigDecimal.valueOf(325_00L, 2)),
                    ),
                    partnerContribution = BigDecimal.valueOf(2275_00L, 2),
                    publicContribution = BigDecimal.valueOf(816_40L, 2),
                    automaticPublicContribution = BigDecimal.valueOf(938_60L, 2),
                    privateContribution = BigDecimal.valueOf(520_00L, 2),
                    total = BigDecimal.valueOf(6500_00L, 2),
                    split = listOf(
                        FinancingSourceBreakdownSplitLine(
                            fundId = 1,
                            value = BigDecimal.valueOf(3900_00L, 2),
                            partnerContribution = BigDecimal.valueOf(2100_00L, 2),
                            publicContribution = BigDecimal.valueOf(753_60L, 2),
                            automaticPublicContribution = BigDecimal.valueOf(866_40L, 2),
                            privateContribution = BigDecimal.valueOf(480_00L, 2),
                            total = BigDecimal.valueOf(6000_00L, 2),
                        ),
                        FinancingSourceBreakdownSplitLine(
                            fundId = 4,
                            value = BigDecimal.valueOf(325_00L, 2),
                            partnerContribution = BigDecimal.valueOf(175_00L, 2),
                            publicContribution = BigDecimal.valueOf(62_80L, 2),
                            automaticPublicContribution = BigDecimal.valueOf(72_20L, 2),
                            privateContribution = BigDecimal.valueOf(40_00L, 2),
                            total = BigDecimal.valueOf(500_00L, 2),
                        ),
                    ),
                ),
                FinancingSourceBreakdownLine(
                    partnerReportId = null,
                    partnerReportNumber = null,
                    spfLine = true,
                    partnerId = null,
                    partnerRole = null,
                    partnerNumber = null,
                    fundsSorted = listOf(
                        Pair(ERDF_FUND, BigDecimal.valueOf(1L)),
                        Pair(NDCI_FUND, BigDecimal.valueOf(2L)),
                    ),
                    partnerContribution = BigDecimal.valueOf(6L),
                    publicContribution = BigDecimal.valueOf(1L),
                    automaticPublicContribution = BigDecimal.valueOf(2L),
                    privateContribution = BigDecimal.valueOf(3L),
                    total = BigDecimal.valueOf(9L),
                    split = listOf(
                        FinancingSourceBreakdownSplitLine(
                            fundId = 1,
                            value = BigDecimal.valueOf(1L),
                            partnerContribution = BigDecimal.valueOf(1_99L, 2),
                            publicContribution = BigDecimal.valueOf(33L, 2),
                            automaticPublicContribution = BigDecimal.valueOf(66L, 2),
                            privateContribution = BigDecimal.valueOf(99L, 2),
                            total = BigDecimal.valueOf(2_99L, 2),
                        ),
                        FinancingSourceBreakdownSplitLine(
                            fundId = 5,
                            value = BigDecimal.valueOf(2L),
                            partnerContribution = BigDecimal.valueOf(3_99L, 2),
                            publicContribution = BigDecimal.valueOf(66L, 2),
                            automaticPublicContribution = BigDecimal.valueOf(1_33L, 2),
                            privateContribution = BigDecimal.valueOf(1_99L, 2),
                            total = BigDecimal.valueOf(5_99L, 2),
                        ),
                    ),
                ),
            ),
            total = FinancingSourceBreakdownLine(
                partnerReportId = null,
                partnerReportNumber = null,
                spfLine = false,
                partnerId = null,
                partnerRole = null,
                partnerNumber = null,
                fundsSorted = listOf(
                    ERDF_FUND to BigDecimal.valueOf(3901_00L, 2),
                    IPA_III_FUND to BigDecimal.valueOf(325_00L, 2),
                    NDCI_FUND to BigDecimal.valueOf(2L),
                ),
                partnerContribution = BigDecimal.valueOf(2281_00L, 2),
                publicContribution = BigDecimal.valueOf(817_40L, 2),
                automaticPublicContribution = BigDecimal.valueOf(940_60L, 2),
                privateContribution = BigDecimal.valueOf(523_00L, 2),
                total = BigDecimal.valueOf(6509_00L, 2),
                split = emptyList(),
            )
        )
    }

    @MockK
    lateinit var projectReportPersistence: ProjectReportPersistence

    @MockK
    lateinit var projectReportFinancialOverviewPersistence: ProjectReportFinancialOverviewPersistence

    @MockK
    lateinit var projectReportCertificateCoFinancingPersistence: ProjectReportCertificateCoFinancingPersistence

    @MockK
    lateinit var projectReportVerificationExpenditurePersistence: ProjectReportVerificationExpenditurePersistence

    @MockK
    lateinit var getPartnerReportFinancialData: GetPartnerReportFinancialData

    @MockK
    lateinit var partnerReportCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence

    @MockK
    private lateinit var reportSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence

    @InjectMockKs
    lateinit var calculator: GetProjectReportFinancingSourceBreakdownCalculator

    @BeforeEach
    fun setup() {
        clearMocks(
            projectReportPersistence,
            projectReportFinancialOverviewPersistence,
            projectReportCertificateCoFinancingPersistence,
            projectReportVerificationExpenditurePersistence,
            getPartnerReportFinancialData,
            partnerReportCoFinancingPersistence,
            reportSpfClaimPersistence,
        )
    }

    @Test
    fun `getFinancingSource - ProjectReport Finalized`() {
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns mockk { every { status } returns ProjectReportStatus.Finalized }
        every { projectReportFinancialOverviewPersistence.getOverviewPerFund(REPORT_ID) } returns listOf(finalizedExpenditureLine)
        every { projectReportFinancialOverviewPersistence.getOverviewSpfPerFund(REPORT_ID) } returns spfExpenditureLine
        every { projectReportCertificateCoFinancingPersistence.getCoFinancing(PROJECT_ID, REPORT_ID).currentVerified } returns
                finalizedCoFinancingCurrentVerified

        assertThat(calculator.getFinancingSource(PROJECT_ID, REPORT_ID)).isEqualTo(breakdownFinalized)
    }

    @Test
    fun `getFinancingSource - ProjectReport InVerification`() {
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns mockk { every { status } returns ProjectReportStatus.InVerification }
        every { projectReportVerificationExpenditurePersistence.getProjectReportExpenditureVerification(REPORT_ID) } returns listOf(inVerificationExpenditure)
        every { getPartnerReportFinancialData.retrievePartnerReportFinancialData(PARTNER_REPORT_ID) } returns inVerificationPartnerReportFinancialData
        every { partnerReportCoFinancingPersistence.getAvailableFunds(PARTNER_REPORT_ID) } returns listOf(ERDF_FUND, IPA_III_FUND)
        every { reportSpfClaimPersistence.getCurrentSpfContributionSplit(REPORT_ID) } returns inVerificationSpf

        assertThat(calculator.getFinancingSource(PROJECT_ID, REPORT_ID)).isEqualTo(breakdownInVerification)
    }
}
