package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview

import io.cloudflight.jems.server.UnitTest
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
            return ProjectReportVerificationExpenditureLine(
                expenditure = expenditure,
                partOfVerificationSample = true,
                deductedByJs = BigDecimal.valueOf(68L),
                deductedByMa = BigDecimal.valueOf(32L),
                amountAfterVerification = BigDecimal.valueOf(100L),
                typologyOfErrorId = 9L,
                parked = false,
                verificationComment = "",
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
            return ProjectReportVerificationExpenditureLine(
                expenditure = expenditure,
                partOfVerificationSample = true,
                deductedByJs = BigDecimal.valueOf(39L),
                deductedByMa = BigDecimal.valueOf(11L),
                amountAfterVerification = BigDecimal.valueOf(300L),
                typologyOfErrorId = 9L,
                parked = false,
                verificationComment = "",
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
            return ProjectReportVerificationExpenditureLine(
                expenditure = expenditure,
                partOfVerificationSample = true,
                deductedByJs = BigDecimal.ZERO,
                deductedByMa = BigDecimal.ZERO,
                amountAfterVerification = BigDecimal.ZERO,
                typologyOfErrorId = 9L,
                parked = true,
                verificationComment = "",
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
            currentlyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(23L),
                office = BigDecimal.valueOf(25L),
                travel = BigDecimal.valueOf(27L),
                external = BigDecimal.valueOf(29_30L, 2),
                equipment = BigDecimal.valueOf(31L),
                infrastructure = BigDecimal.valueOf(33_30L, 2),
                other = BigDecimal.valueOf(35L),
                lumpSum = BigDecimal.valueOf(37L),
                unitCost = BigDecimal.valueOf(39L),
                sum = BigDecimal.valueOf(279_60L, 2),
            ),
            totalEligibleAfterControl = mockk(),
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
            currentlyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(81L),
                office = BigDecimal.valueOf(91L),
                travel = BigDecimal.valueOf(101L),
                external = BigDecimal.valueOf(110_50L, 2),
                equipment = BigDecimal.valueOf(112_60L, 2),
                infrastructure = BigDecimal.valueOf(114_50L, 2),
                other = BigDecimal.valueOf(125L),
                lumpSum = BigDecimal.valueOf(135L),
                unitCost = BigDecimal.valueOf(145L),
                sum = BigDecimal.valueOf(1015_60L, 2),
            ),
            totalEligibleAfterControl = mockk(),
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
                    requestedByPartner = BigDecimal.valueOf(1015_60L, 2),
                    requestedByPartnerWithoutFlatRates = BigDecimal.valueOf(718_60L, 2),
                    inVerificationSample = BigDecimal.valueOf(620L),
                    inVerificationSamplePercentage = BigDecimal.valueOf(86_28L, 2),
                    parked = BigDecimal.valueOf(446_87L, 2),
                    deductedByJs = BigDecimal.valueOf(63_37L, 2),
                    deductedByMa = BigDecimal.valueOf(17_87L, 2),
                    deducted = BigDecimal.valueOf(81_23L, 2),
                    afterVerification = BigDecimal.valueOf(487_50L, 2),
                    afterVerificationPercentage = BigDecimal.valueOf(48_00L, 2),
                ),
            ),
            total = VerificationWorkOverviewLine(
                partnerId = null,
                partnerRole = null,
                partnerNumber = null,
                partnerReportId = null,
                partnerReportNumber = null,
                requestedByPartner = BigDecimal.valueOf(1295_20L, 2),
                requestedByPartnerWithoutFlatRates = BigDecimal.valueOf(923_20L, 2),
                inVerificationSample = BigDecimal.valueOf(820L),
                inVerificationSamplePercentage = BigDecimal.valueOf(88_82L, 2),
                parked = BigDecimal.valueOf(446_87L, 2),
                deductedByJs = BigDecimal.valueOf(158_43L, 2),
                deductedByMa = BigDecimal.valueOf(62_60L, 2),
                deducted = BigDecimal.valueOf(221_03L, 2),
                afterVerification = BigDecimal.valueOf(627_30L, 2),
                afterVerificationPercentage = BigDecimal.valueOf(48_43L, 2),
            ),
        )
    }

    @MockK
    private lateinit var verificationExpenditurePersistence: ProjectReportVerificationExpenditurePersistence
    @MockK
    private lateinit var partnerReportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence

    @InjectMockKs
    lateinit var calculator: GetProjectReportVerificationWorkOverviewCalculator

    @BeforeEach
    fun setup() {
        clearMocks(verificationExpenditurePersistence, partnerReportExpenditureCostCategoryPersistence)
    }

    @Test
    fun getWorkOverviewPerPartner() {
        every { verificationExpenditurePersistence.getProjectReportExpenditureVerification(REPORT_ID) } returns expenditures
        every { partnerReportExpenditureCostCategoryPersistence.getCostCategories(4L, 400L) } returns partner_4
        every { partnerReportExpenditureCostCategoryPersistence.getCostCategories(5L, 500L) } returns partner_5

        assertThat(calculator.getWorkOverviewPerPartner(REPORT_ID)).isEqualTo(expectedOverview)
    }

}
