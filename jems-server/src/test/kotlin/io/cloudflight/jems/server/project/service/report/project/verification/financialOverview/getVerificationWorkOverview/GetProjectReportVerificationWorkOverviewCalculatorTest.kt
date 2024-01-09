package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverviewLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal


class GetProjectReportVerificationWorkOverviewCalculatorTest : UnitTest() {

    companion object {
        private const val REPORT_ID = 775L

        private fun expenditure_partner_4(): ProjectReportVerificationExpenditureLine {
            val expenditure = mockk<ProjectPartnerReportExpenditureItem>()
            every { expenditure.id } returns 4999L
            every { expenditure.partnerId } returns 4L
            every { expenditure.partnerRole } returns ProjectPartnerRole.LEAD_PARTNER
            every { expenditure.partnerNumber } returns 41
            every { expenditure.partnerReportId } returns 400L
            every { expenditure.partnerReportNumber } returns 410
            every { expenditure.lumpSum } returns null
            every { expenditure.costCategory } returns ReportBudgetCategory.InfrastructureCosts
            every { expenditure.declaredAmountAfterSubmission } returns BigDecimal.valueOf(230L)
            every { expenditure.certifiedAmount } returns BigDecimal.valueOf(200L)
            every { expenditure.parkingMetadata } returns null
            every { expenditure.unitCost } returns null
            every { expenditure.investment } returns null
            return ProjectReportVerificationExpenditureLine(
                expenditure = expenditure,
                partOfVerificationSample = true,
                deductedByJs = BigDecimal.valueOf(68L),
                deductedByMa = BigDecimal.valueOf(32L),
                amountAfterVerification = BigDecimal.valueOf(100L),
                typologyOfErrorId = 9L,
                parked = false,
                verificationComment = "",
                parkedOn = null
            )
        }

        private fun expenditure_partner_5a(): ProjectReportVerificationExpenditureLine {
            val expenditure = mockk<ProjectPartnerReportExpenditureItem>()
            every { expenditure.id } returns 5998L
            every { expenditure.partnerId } returns 5L
            every { expenditure.partnerRole } returns ProjectPartnerRole.PARTNER
            every { expenditure.partnerNumber } returns 51
            every { expenditure.partnerReportId } returns 500L
            every { expenditure.partnerReportNumber } returns 510
            every { expenditure.lumpSum } returns null
            every { expenditure.costCategory } returns ReportBudgetCategory.ExternalCosts
            every { expenditure.declaredAmountAfterSubmission } returns BigDecimal.valueOf(390L)
            every { expenditure.certifiedAmount } returns BigDecimal.valueOf(350L)
            every { expenditure.parkingMetadata } returns null
            every { expenditure.unitCost } returns null
            every { expenditure.investment } returns null
            return ProjectReportVerificationExpenditureLine(
                expenditure = expenditure,
                partOfVerificationSample = true,
                deductedByJs = BigDecimal.valueOf(39L),
                deductedByMa = BigDecimal.valueOf(11L),
                amountAfterVerification = BigDecimal.valueOf(300L),
                typologyOfErrorId = 9L,
                parked = false,
                verificationComment = "",
                parkedOn = null
            )
        }

        private fun expenditure_partner_5b_parked(): ProjectReportVerificationExpenditureLine {
            val expenditure = mockk<ProjectPartnerReportExpenditureItem>()
            every { expenditure.id } returns 5999L
            every { expenditure.partnerId } returns 5L
            every { expenditure.partnerRole } returns ProjectPartnerRole.PARTNER
            every { expenditure.partnerNumber } returns 51
            every { expenditure.partnerReportId } returns 500L
            every { expenditure.partnerReportNumber } returns 510
            every { expenditure.lumpSum } returns null
            every { expenditure.costCategory } returns ReportBudgetCategory.TravelAndAccommodationCosts
            every { expenditure.declaredAmountAfterSubmission } returns BigDecimal.valueOf(275L)
            every { expenditure.certifiedAmount } returns BigDecimal.valueOf(270L)
            every { expenditure.parkingMetadata } returns mockk()
            every { expenditure.unitCost } returns null
            every { expenditure.investment } returns null
            return ProjectReportVerificationExpenditureLine(
                expenditure = expenditure,
                partOfVerificationSample = true,
                deductedByJs = BigDecimal.ZERO,
                deductedByMa = BigDecimal.ZERO,
                amountAfterVerification = BigDecimal.ZERO,
                typologyOfErrorId = 9L,
                parked = true,
                verificationComment = "",
                parkedOn = null
            )
        }

        private val expenditures = listOf(
            expenditure_partner_4(),
            expenditure_partner_5a(),
            expenditure_partner_5b_parked(),
        )

        private val partner_4 = ReportExpenditureCostCategory(
            options = ProjectPartnerBudgetOptions(
                partnerId = 4L,
                officeAndAdministrationOnStaffCostsFlatRate = null,
                officeAndAdministrationOnDirectCostsFlatRate = 20,
                travelAndAccommodationOnStaffCostsFlatRate = 10,
                staffCostsFlatRate = 15,
                otherCostsOnStaffCostsFlatRate = null,
            ),
            totalsFromAF = mockk(),
            currentlyReported = mockk(),
            totalEligibleAfterControl = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(23L),
                office = BigDecimal.valueOf(25L),
                travel = BigDecimal.valueOf(27L),
                external = mockk(),
                equipment = mockk(),
                infrastructure = mockk(),
                other = BigDecimal.valueOf(35L),
                lumpSum = mockk(),
                unitCost = mockk(),
                spfCost = mockk(),
                sum = BigDecimal.valueOf(279_60L, 2),
            ),
            previouslyReported = mockk(),
            previouslyValidated = mockk(),
            currentlyReportedParked = mockk(),
            currentlyReportedReIncluded = mockk(),
            previouslyReportedParked = mockk(),
        )

        private val partner_5 = ReportExpenditureCostCategory(
            options = ProjectPartnerBudgetOptions(
                partnerId = 4L,
                officeAndAdministrationOnStaffCostsFlatRate = 25,
                officeAndAdministrationOnDirectCostsFlatRate = null,
                travelAndAccommodationOnStaffCostsFlatRate = null,
                staffCostsFlatRate = 50,
                otherCostsOnStaffCostsFlatRate = 15,
            ),
            totalsFromAF = mockk(),
            currentlyReported = mockk(),
            totalEligibleAfterControl = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(81L),
                office = BigDecimal.valueOf(91L),
                travel = BigDecimal.valueOf(101L),
                external = mockk(),
                equipment = mockk(),
                infrastructure = mockk(),
                other = BigDecimal.valueOf(125L),
                lumpSum = mockk(),
                unitCost = mockk(),
                spfCost = mockk(),
                sum = BigDecimal.valueOf(1015_60L, 2),
            ),
            previouslyReported = mockk(),
            previouslyValidated = mockk(),
            currentlyReportedParked = mockk(),
            currentlyReportedReIncluded = mockk(),
            previouslyReportedParked = mockk(),
        )

        private val expectedOverview = VerificationWorkOverview(
            certificates = listOf(
                VerificationWorkOverviewLine(
                    partnerId = 4L,
                    partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                    partnerNumber = 41,
                    partnerReportId = 400,
                    partnerReportNumber = 410,
                    spfLine = false,
                    requestedByPartner = BigDecimal.valueOf(279_60L, 2),
                    requestedByPartnerWithoutFlatRates = BigDecimal.valueOf(204_60L, 2),
                    inVerificationSample = BigDecimal.valueOf(200L),
                    inVerificationSamplePercentage = BigDecimal.valueOf(97_75L, 2),
                    parked = BigDecimal.valueOf(0L, 2),
                    deductedByJs = BigDecimal.valueOf(95_06L, 2),
                    deductedByMa = BigDecimal.valueOf(44_73L, 2),
                    deducted = BigDecimal.valueOf(139_80L, 2),
                    afterVerification = BigDecimal.valueOf(139_80L, 2),
                    afterVerificationPercentage = BigDecimal.valueOf(50_00L, 2),
                ),
                VerificationWorkOverviewLine(
                    partnerId = 5L,
                    partnerRole = ProjectPartnerRole.PARTNER,
                    partnerNumber = 51,
                    partnerReportId = 500,
                    partnerReportNumber = 510,
                    spfLine = false,
                    requestedByPartner = BigDecimal.valueOf(1015_60L, 2),
                    requestedByPartnerWithoutFlatRates = BigDecimal.valueOf(718_60L, 2),
                    inVerificationSample = BigDecimal.valueOf(620L),
                    inVerificationSamplePercentage = BigDecimal.valueOf(86_28L, 2),
                    parked = BigDecimal.valueOf(438_75L, 2),
                    deductedByJs = BigDecimal.valueOf(63_37L, 2),
                    deductedByMa = BigDecimal.valueOf(17_87L, 2),
                    deducted = BigDecimal.valueOf(89_35L, 2),
                    afterVerification = BigDecimal.valueOf(487_50L, 2),
                    afterVerificationPercentage = BigDecimal.valueOf(48_00L, 2),
                ),
            ),
            total = VerificationWorkOverviewLine(
                partnerId = 0L,
                partnerRole = null,
                partnerNumber = 0,
                partnerReportId = 0L,
                partnerReportNumber = 0,
                spfLine = false,
                requestedByPartner = BigDecimal.valueOf(1295_20L, 2),
                requestedByPartnerWithoutFlatRates = BigDecimal.valueOf(923_20L, 2),
                inVerificationSample = BigDecimal.valueOf(820L),
                inVerificationSamplePercentage = BigDecimal.valueOf(88_82L, 2),
                parked = BigDecimal.valueOf(438_75L, 2),
                deductedByJs = BigDecimal.valueOf(158_43L, 2),
                deductedByMa = BigDecimal.valueOf(62_60L, 2),
                deducted = BigDecimal.valueOf(229_15L, 2),
                afterVerification = BigDecimal.valueOf(627_30L, 2),
                afterVerificationPercentage = BigDecimal.valueOf(48_43L, 2),
            ),
        )

        private val expectedWithSpfOverview = VerificationWorkOverview(
            certificates = expectedOverview.certificates.plus(
                VerificationWorkOverviewLine(
                    partnerId = 0L,
                    partnerRole = null,
                    partnerNumber = 0,
                    partnerReportId = 0,
                    partnerReportNumber = 0,
                    spfLine = true,
                    requestedByPartner = BigDecimal.valueOf(189L, 1),
                    requestedByPartnerWithoutFlatRates = BigDecimal.valueOf(189L, 1),
                    inVerificationSample = BigDecimal.ZERO,
                    inVerificationSamplePercentage = null,
                    parked = BigDecimal.ZERO,
                    deductedByJs = BigDecimal.ZERO,
                    deductedByMa = BigDecimal.ZERO,
                    deducted = BigDecimal.ZERO,
                    afterVerification = BigDecimal.valueOf(189L, 1),
                    afterVerificationPercentage = BigDecimal.valueOf(100L),
                ),
            ),
            total = VerificationWorkOverviewLine(
                partnerId = 0L,
                partnerRole = null,
                partnerNumber = 0,
                partnerReportId = 0L,
                partnerReportNumber = 0,
                spfLine = false,
                requestedByPartner = BigDecimal.valueOf(1314_10L, 2),
                requestedByPartnerWithoutFlatRates = BigDecimal.valueOf(942_10L, 2),
                inVerificationSample = BigDecimal.valueOf(820L),
                inVerificationSamplePercentage = BigDecimal.valueOf(87_04L, 2),
                parked = BigDecimal.valueOf(438_75L, 2),
                deductedByJs = BigDecimal.valueOf(158_43L, 2),
                deductedByMa = BigDecimal.valueOf(62_60L, 2),
                deducted = BigDecimal.valueOf(229_15L, 2),
                afterVerification = BigDecimal.valueOf(646_20L, 2),
                afterVerificationPercentage = BigDecimal.valueOf(49_17L, 2),
            ),
        )

    }

    @MockK
    private lateinit var verificationExpenditurePersistence: ProjectReportVerificationExpenditurePersistence
    @MockK
    private lateinit var partnerReportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence
    @MockK
    private lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    private lateinit var callPersistence: CallPersistence
    @MockK
    private lateinit var reportSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence

    @InjectMockKs
    lateinit var calculator: GetProjectReportVerificationWorkOverviewCalculator

    @BeforeEach
    fun setup() {
        clearMocks(verificationExpenditurePersistence, partnerReportExpenditureCostCategoryPersistence,
            reportPersistence, callPersistence, reportSpfClaimPersistence)
    }

    @Test
    fun `getWorkOverviewPerPartner - without spf`() {
        every { verificationExpenditurePersistence.getProjectReportExpenditureVerification(REPORT_ID) } returns expenditures
        every {
            partnerReportExpenditureCostCategoryPersistence.getCostCategoriesFor(setOf(400L, 500L,))
        } returns mapOf(400L to partner_4, 500L to partner_5)

        every { reportPersistence.getReportByIdUnSecured(REPORT_ID).projectId } returns 75L
        every { callPersistence.getCallByProjectId(75L).isSpf() } returns false

        assertThat(calculator.getWorkOverviewPerPartner(REPORT_ID)).isEqualTo(expectedOverview)
    }

    @Test
    fun `getWorkOverviewPerPartner - with spf`() {
        every { verificationExpenditurePersistence.getProjectReportExpenditureVerification(REPORT_ID) } returns expenditures
        every {
            partnerReportExpenditureCostCategoryPersistence.getCostCategoriesFor(setOf(400L, 500L,))
        } returns mapOf(400L to partner_4, 500L to partner_5)

        every { reportPersistence.getReportByIdUnSecured(REPORT_ID).projectId } returns 76L
        every { callPersistence.getCallByProjectId(76L).isSpf() } returns true
        every { reportSpfClaimPersistence.getCurrentSpfContribution(REPORT_ID).sum } returns BigDecimal.valueOf(189L, 1)

        assertThat(calculator.getWorkOverviewPerPartner(REPORT_ID)).isEqualTo(expectedWithSpfOverview)
    }

}
