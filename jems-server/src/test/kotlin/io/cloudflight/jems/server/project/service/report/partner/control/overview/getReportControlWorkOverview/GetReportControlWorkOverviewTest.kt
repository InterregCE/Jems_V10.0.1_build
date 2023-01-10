package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetReportControlWorkOverviewTest : UnitTest() {

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
            currentlyReported = mockk(),
            totalEligibleAfterControl = mockk(),
            previouslyReported = mockk(),
        )

        fun expenditure(
            id: Long,
            partOfSample: Boolean,
            declaredAmount: BigDecimal?,
            certified: BigDecimal,
            isParked: Boolean
        ): ProjectPartnerReportExpenditureVerification {
            val expenditure = mockk<ProjectPartnerReportExpenditureVerification>()
            every { expenditure.id } returns id
            every { expenditure.parked } returns isParked
            every { expenditure.partOfSample } returns partOfSample
            every { expenditure.declaredAmountAfterSubmission } returns
                (declaredAmount ?: BigDecimal.valueOf(99999) /* should be ignored */)
            every { expenditure.certifiedAmount } returns certified
            every { expenditure.lumpSumId } returns null
            every { expenditure.costCategory } returns ReportBudgetCategory.StaffCosts
            return expenditure
        }

    }

    @MockK
    private lateinit var reportCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence
    @MockK
    private lateinit var reportControlExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence
    @MockK
    private lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence

    @InjectMockKs
    private lateinit var interactor: GetReportControlWorkOverview

    @BeforeEach
    fun setup() {
        clearMocks(reportCoFinancingPersistence, reportControlExpenditurePersistence, reportExpenditureCostCategoryPersistence)
    }

    @Test
    fun get() {
        every { reportControlExpenditurePersistence
            .getPartnerControlReportExpenditureVerification(PARTNER_ID, reportId = 22L)
        } returns listOf(
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

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 22L) } returns costOptions
        every { reportCoFinancingPersistence.getReportCurrentSum(PARTNER_ID, reportId = 22L) } returns BigDecimal.TEN

        assertThat(interactor.get(PARTNER_ID, reportId = 22L)).isEqualTo(
            ControlWorkOverview(
                declaredByPartner = BigDecimal.TEN,
                inControlSample = BigDecimal.ONE,
                parked = BigDecimal.valueOf(115, 2),
                deductedByControl = BigDecimal.valueOf(724L, 2),
                eligibleAfterControl = BigDecimal.valueOf(161L, 2),
                eligibleAfterControlPercentage = BigDecimal.valueOf(1610L, 2),
            )
        )
    }

}
