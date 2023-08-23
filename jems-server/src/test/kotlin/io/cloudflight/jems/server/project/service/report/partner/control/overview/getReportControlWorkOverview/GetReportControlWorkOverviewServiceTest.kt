package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal

internal class GetReportControlWorkOverviewServiceTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 592L

        private val costOptions = ReportExpenditureCostCategory(
            options = ProjectPartnerBudgetOptions(
                partnerId = PARTNER_ID,
                officeAndAdministrationOnStaffCostsFlatRate = null,
                officeAndAdministrationOnDirectCostsFlatRate = null,
                travelAndAccommodationOnStaffCostsFlatRate = 15,
                staffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
            ),
            totalsFromAF = mockk(),
            currentlyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(110),
                office = BigDecimal.valueOf(111),
                travel = BigDecimal.valueOf(112),
                external = BigDecimal.valueOf(113),
                equipment = BigDecimal.valueOf(114),
                infrastructure = BigDecimal.valueOf(115),
                other = BigDecimal.valueOf(116),
                lumpSum = BigDecimal.valueOf(117),
                unitCost = BigDecimal.valueOf(118),
                sum = BigDecimal.valueOf(119),
            ),
            currentlyReportedParked = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(120),
                office = BigDecimal.valueOf(121),
                travel = BigDecimal.valueOf(122),
                external = BigDecimal.valueOf(123),
                equipment = BigDecimal.valueOf(124),
                infrastructure = BigDecimal.valueOf(125),
                other = BigDecimal.valueOf(126),
                lumpSum = BigDecimal.valueOf(127),
                unitCost = BigDecimal.valueOf(128),
                sum = BigDecimal.valueOf(129),
            ),
            currentlyReportedReIncluded = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(130),
                office = BigDecimal.valueOf(131),
                travel = BigDecimal.valueOf(132),
                external = BigDecimal.valueOf(133),
                equipment = BigDecimal.valueOf(134),
                infrastructure = BigDecimal.valueOf(135),
                other = BigDecimal.valueOf(136),
                lumpSum = BigDecimal.valueOf(137),
                unitCost = BigDecimal.valueOf(138),
                sum = BigDecimal.valueOf(139),
            ),
            totalEligibleAfterControl = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(19),
                office = BigDecimal.valueOf(18),
                travel = BigDecimal.valueOf(17),
                external = BigDecimal.valueOf(16),
                equipment = BigDecimal.valueOf(15),
                infrastructure = BigDecimal.valueOf(14),
                other = BigDecimal.valueOf(13),
                lumpSum = BigDecimal.valueOf(12),
                unitCost = BigDecimal.valueOf(11),
                sum = BigDecimal.valueOf(10),
            ),
            previouslyReported = mockk(),
            previouslyReportedParked = mockk(),
            previouslyValidated = mockk()
        )

        fun expenditure(
            id: Long,
            partOfSample: Boolean,
            declaredAmount: BigDecimal?,
            certified: BigDecimal,
            isParked: Boolean
        ) = ProjectPartnerReportExpenditureVerification(
            id = id,
            number = -1,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            gdpr = false,
            investmentId = -2L,
            contractId = -3L,
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
            declaredAmount = declaredAmount ?: BigDecimal.valueOf(99999),
            currencyCode = "",
            currencyConversionRate = mockk(),
            declaredAmountAfterSubmission = declaredAmount,
            attachment = mockk(),
            partOfSample = partOfSample,
            certifiedAmount = certified,
            deductedAmount = mockk(),
            typologyOfErrorId = null,
            verificationComment = "comment dummy",
            parked = isParked,
            parkedOn = null,
            parkingMetadata = null,
            partOfSampleLocked = false,
        )

        private val listOfExpenditures = listOf(
            expenditure(
                554L,
                partOfSample = true,
                declaredAmount = BigDecimal.ONE,
                certified = BigDecimal.valueOf(9, 1),
                isParked = true
            ),
            expenditure(
                555L,
                partOfSample = false,
                declaredAmount = null,
                certified = BigDecimal.valueOf(5, 1),
                isParked = false
            ),
            expenditure(
                556L,
                partOfSample = false,
                declaredAmount = BigDecimal.valueOf(33333),
                certified = BigDecimal.ZERO,
                isParked = false,
            ),
        )

    }

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    private lateinit var reportControlExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence

    @MockK
    private lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence

    @InjectMockKs
    private lateinit var getReportControlWorkOverviewService: GetReportControlWorkOverviewService

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence, reportControlExpenditurePersistence, reportExpenditureCostCategoryPersistence)
    }

    @ParameterizedTest(name = "get (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Certified"])
    fun get(status: ReportStatus) {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 22L) } returns report

        every {
            reportControlExpenditurePersistence
                .getPartnerControlReportExpenditureVerification(PARTNER_ID, reportId = 22L)
        } returns listOfExpenditures

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 22L) } returns costOptions

        assertThat(getReportControlWorkOverviewService.get(PARTNER_ID, reportId = 22L)).isEqualTo(
            ControlWorkOverview(
                declaredByPartner = BigDecimal.valueOf(119L),
                declaredByPartnerFlatRateSum = BigDecimal.valueOf(112L),
                inControlSample = BigDecimal.valueOf(1L),
                inControlSamplePercentage = BigDecimal.valueOf(1429L, 2),
                parked = BigDecimal.valueOf(129L),
                deductedByControl = BigDecimal.valueOf(-20L),
                eligibleAfterControl = BigDecimal.valueOf(10L),
                eligibleAfterControlPercentage = BigDecimal.valueOf(840L, 2),
            )
        )
    }

    @ParameterizedTest(name = "get - closed (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Certified"], mode = EnumSource.Mode.EXCLUDE)
    fun `get - closed`(status: ReportStatus) {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 22L) } returns report

        every {
            reportControlExpenditurePersistence
                .getPartnerControlReportExpenditureVerification(PARTNER_ID, reportId = 22L)
        } returns listOfExpenditures

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 22L) } returns costOptions

        assertThat(getReportControlWorkOverviewService.get(PARTNER_ID, reportId = 22L)).isEqualTo(
            ControlWorkOverview(
                declaredByPartner = BigDecimal.valueOf(119L),
                declaredByPartnerFlatRateSum = BigDecimal.valueOf(112L),
                inControlSample = BigDecimal.valueOf(1L),
                inControlSamplePercentage = BigDecimal.valueOf(1429L, 2),
                parked = BigDecimal.valueOf(115, 2),
                deductedByControl = BigDecimal.valueOf(11624L, 2),
                eligibleAfterControl = BigDecimal.valueOf(161L, 2),
                eligibleAfterControlPercentage = BigDecimal.valueOf(135L, 2),
            )
        )
    }

    @Test
    fun `get - flat rate sum is calculated correctly`() {
        val options = ProjectPartnerBudgetOptions(
            partnerId = PARTNER_ID,
            officeAndAdministrationOnStaffCostsFlatRate = 2,
            officeAndAdministrationOnDirectCostsFlatRate = null,
            travelAndAccommodationOnStaffCostsFlatRate = null,
            staffCostsFlatRate = 10,
            otherCostsOnStaffCostsFlatRate = 5,
        )

        val currentlyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(110),
                office = BigDecimal.valueOf(111),
                travel = BigDecimal.valueOf(112),
                external = BigDecimal.valueOf(113),
                equipment = BigDecimal.valueOf(114),
                infrastructure = BigDecimal.valueOf(115),
                other = BigDecimal.valueOf(116),
                lumpSum = BigDecimal.valueOf(117),
                unitCost = BigDecimal.valueOf(118),
                sum = BigDecimal.valueOf(1026),
        )

        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns ReportStatus.InControl
        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 22L) } returns report

        every {
            reportControlExpenditurePersistence
                .getPartnerControlReportExpenditureVerification(PARTNER_ID, reportId = 22L)
        } returns listOfExpenditures

        every {
            reportExpenditureCostCategoryPersistence.getCostCategories(
                PARTNER_ID,
                reportId = 22L
            )
        } returns costOptions.copy(options = options, currentlyReported = currentlyReported)

        assertThat(getReportControlWorkOverviewService.get(PARTNER_ID, reportId = 22L)).isEqualTo(
            ControlWorkOverview(
                declaredByPartner = BigDecimal.valueOf(1026L),
                declaredByPartnerFlatRateSum = BigDecimal.valueOf(337L),
                inControlSample = BigDecimal.valueOf(1L),
                inControlSamplePercentage = BigDecimal.valueOf(15L, 2),
                parked = BigDecimal.valueOf(0, 2),
                deductedByControl = BigDecimal.valueOf(102600L, 2),
                eligibleAfterControl = BigDecimal.valueOf(0, 2),
                eligibleAfterControlPercentage = BigDecimal.valueOf(0, 2),
            )
        )
    }

}
