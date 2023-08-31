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
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
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
import java.time.LocalDate


class GetProjectReportFinancingSourceBreakdownCalculatorTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 13L
        const val REPORT_ID = 14L

        val finalizedExpenditureLine = FinancingSourceBreakdownLine(
            partnerReportId = 1L,
            partnerReportNumber = 2,
            partnerId = 3,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 4,
            fundsSorted = listOf(
                Pair(ERDF_FUND, 74999.97.toScaledBigDecimal()),
                Pair(IPA_III_FUND, 6249.99.toScaledBigDecimal()),
                Pair(NDCI_FUND, 24999.99.toScaledBigDecimal()),
            ),
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.21.toScaledBigDecimal(),
            automaticPublicContribution = 5938.23.toScaledBigDecimal(),
            privateContribution = 1199.51.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = listOf(
                FinancingSourceBreakdownSplitLine(
                    fundId = 1L,
                    value = 74999.97.toScaledBigDecimal(),
                    partnerContribution = 13235.30.toScaledBigDecimal(),
                    publicContribution = 8196.86.toScaledBigDecimal(),
                    automaticPublicContribution = 4191.69.toScaledBigDecimal(),
                    privateContribution = 846.72.toScaledBigDecimal(),
                    total = 88235.27.toScaledBigDecimal(),
                ),
                FinancingSourceBreakdownSplitLine(
                    fundId = 4L,
                    value = 6249.99.toScaledBigDecimal(),
                    partnerContribution = 1102.94.toScaledBigDecimal(),
                    publicContribution = 683.07.toScaledBigDecimal(),
                    automaticPublicContribution = 349.30.toScaledBigDecimal(),
                    privateContribution = 70.55.toScaledBigDecimal(),
                    total = 7352.93.toScaledBigDecimal(),
                ),
                FinancingSourceBreakdownSplitLine(
                    fundId = 5L,
                    value = 24999.99.toScaledBigDecimal(),
                    partnerContribution = 4411.77.toScaledBigDecimal(),
                    publicContribution = 2732.28.toScaledBigDecimal(),
                    automaticPublicContribution = 1397.23.toScaledBigDecimal(),
                    privateContribution = 282.24.toScaledBigDecimal(),
                    total = 29411.76.toScaledBigDecimal(),
                ),
            )
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
                partnerReportId = 1L,
                partnerReportNumber = 2,
                lumpSum = mockk(),
                unitCost = mockk(),
                gdpr = false,
                costCategory = ReportBudgetCategory.EquipmentCosts,
                investment = mockk(),
                contract = mockk(),
                internalReferenceNumber = "internal-1",
                invoiceNumber = "invoice-1",
                invoiceDate = LocalDate.of(2022, 1, 1),
                dateOfPayment = LocalDate.of(2022, 2, 1),
                description = emptySet(),
                comment = emptySet(),
                totalValueInvoice = 22.toScaledBigDecimal(),
                vat = 18.toScaledBigDecimal(),
                numberOfUnits = 0.00.toScaledBigDecimal(),
                pricePerUnit = 0.00.toScaledBigDecimal(),
                declaredAmount = 31.2.toScaledBigDecimal(),
                currencyCode = "CZK",
                currencyConversionRate = 24.toScaledBigDecimal(),
                declaredAmountAfterSubmission = 1.30.toScaledBigDecimal(),
                attachment = null,

                partOfSample = false,
                partOfSampleLocked = false,
                certifiedAmount = 101.toScaledBigDecimal(),
                deductedAmount = 101.toScaledBigDecimal(),
                typologyOfErrorId = null,
                parked = false,
                verificationComment = null,

                parkingMetadata = null
            ),
            partOfVerificationSample = false,
            deductedByJs = 0.00.toScaledBigDecimal(),
            deductedByMa = 0.00.toScaledBigDecimal(),
            amountAfterVerification = 124999.96.toScaledBigDecimal(),
            typologyOfErrorId = null,
            parked = true,
            verificationComment = "VERIFICATION COMM"
        )

        private val inVerificationPartnerReportFinancialData = PartnerReportFinancialData(
            coFinancingFromAF = listOf(
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = ERDF_FUND,
                    percentage = 60.toScaledBigDecimal(),
                ),
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = IPA_III_FUND,
                    percentage = 5.toScaledBigDecimal(),
                ),
                ProjectPartnerCoFinancing(
                    fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                    fund = NDCI_FUND,
                    percentage = 20.toScaledBigDecimal(),
                ),
            ),
            contributionsFromAF = ProjectPartnerReportContributionOverview(
                public = ProjectPartnerReportContributionRow(
                    amount = 11612.22.toScaledBigDecimal(),
                    previouslyReported = 0.00.toScaledBigDecimal(),
                    currentlyReported = 11612.22.toScaledBigDecimal(),
                    totalReportedSoFar = 11612.22.toScaledBigDecimal(),
                ),
                automaticPublic = ProjectPartnerReportContributionRow(
                    amount = 5938.24.toScaledBigDecimal(),
                    previouslyReported = 0.toScaledBigDecimal(),
                    currentlyReported = 5938.24.toScaledBigDecimal(),
                    totalReportedSoFar = 5938.24.toScaledBigDecimal(),
                ),
                private = ProjectPartnerReportContributionRow(
                    amount = 1199.52.toScaledBigDecimal(),
                    previouslyReported = 0.00.toScaledBigDecimal(),
                    currentlyReported = 1199.52.toScaledBigDecimal(),
                    totalReportedSoFar = 1199.52.toScaledBigDecimal(),
                ),
                total = ProjectPartnerReportContributionRow(
                    amount = 124999.96.toScaledBigDecimal(),
                    previouslyReported = 0.00.toScaledBigDecimal(),
                    currentlyReported = 124999.96.toScaledBigDecimal(),
                    totalReportedSoFar = 124999.96.toScaledBigDecimal(),
                ),
            ),
            totalEligibleBudgetFromAF = 124999.96.toScaledBigDecimal(),
            flatRatesFromAF = ProjectPartnerBudgetOptions(
                partnerId = 125L,
                officeAndAdministrationOnStaffCostsFlatRate = 0,
                officeAndAdministrationOnDirectCostsFlatRate = 0,
                travelAndAccommodationOnStaffCostsFlatRate = 0,
                staffCostsFlatRate = 0,
                otherCostsOnStaffCostsFlatRate = 0,
            ),
        )

        private val financingSourceBreakdown = FinancingSourceBreakdown(
            sources = listOf(finalizedExpenditureLine),
            total = FinancingSourceBreakdownLine(
                partnerReportId = null,
                partnerReportNumber = null,
                partnerId = null,
                partnerRole = null,
                partnerNumber = null,
                fundsSorted = listOf(
                    ERDF_FUND to 74999.97.toScaledBigDecimal(),
                    IPA_III_FUND to 6249.99.toScaledBigDecimal(),
                    NDCI_FUND to 24999.99.toScaledBigDecimal(),
                ),
                partnerContribution = 18750.01.toScaledBigDecimal(),
                publicContribution = 11612.21.toScaledBigDecimal(),
                automaticPublicContribution = 5938.23.toScaledBigDecimal(),
                privateContribution = 1199.51.toScaledBigDecimal(),
                total = 124999.96.toScaledBigDecimal(),
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
        )
    }

    @Test
    fun `getFinancingSource - ProjectReport Finalized`() {
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns mockk { every { status } returns ProjectReportStatus.Finalized }
        every { projectReportFinancialOverviewPersistence.getOverviewPerFund(REPORT_ID) } returns listOf(finalizedExpenditureLine)
        val coFinancing = mockk<ReportCertificateCoFinancing> { every { currentVerified } returns finalizedCoFinancingCurrentVerified }
        every { projectReportCertificateCoFinancingPersistence.getCoFinancing(PROJECT_ID, REPORT_ID) } returns coFinancing
        every { partnerReportCoFinancingPersistence.getAvailableFunds(1L) } returns listOf(ERDF_FUND, IPA_III_FUND, NDCI_FUND)

        assertThat(calculator.getFinancingSource(PROJECT_ID, REPORT_ID)).isEqualTo(financingSourceBreakdown)
    }

    @Test
    fun `getFinancingSource - ProjectReport InVerification`() {
        every { projectReportPersistence.getReportById(PROJECT_ID, REPORT_ID) } returns mockk { every { status } returns ProjectReportStatus.InVerification }
        every { projectReportVerificationExpenditurePersistence.getProjectReportExpenditureVerification(REPORT_ID) } returns listOf(inVerificationExpenditure)
        every { getPartnerReportFinancialData.retrievePartnerReportFinancialData(inVerificationExpenditure.expenditure.partnerReportId) } returns
                inVerificationPartnerReportFinancialData
        every { partnerReportCoFinancingPersistence.getAvailableFunds(1L) } returns listOf(ERDF_FUND, IPA_III_FUND, NDCI_FUND)

        assertThat(calculator.getFinancingSource(PROJECT_ID, REPORT_ID)).isEqualTo(financingSourceBreakdown)
    }
}
