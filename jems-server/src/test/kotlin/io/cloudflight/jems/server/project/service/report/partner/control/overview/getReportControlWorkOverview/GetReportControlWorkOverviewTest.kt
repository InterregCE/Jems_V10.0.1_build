package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.expenditure.control.ProjectReportControlExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportExpenditureCostCategoryPersistence
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

        val costOptions = ReportExpenditureCostCategory(
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
            previouslyReported = mockk(),
        )

        private fun expenditure(
            id: Long,
            partOfSample: Boolean,
            declaredAmount: BigDecimal?,
            certified: BigDecimal,
        ): ProjectPartnerReportExpenditureVerification {
            val expenditure = mockk<ProjectPartnerReportExpenditureVerification>()
            every { expenditure.id } returns id
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
    private lateinit var reportCoFinancingPersistence: ProjectReportExpenditureCoFinancingPersistence
    @MockK
    private lateinit var reportControlExpenditurePersistence: ProjectReportControlExpenditurePersistence
    @MockK
    private lateinit var reportExpenditureCostCategoryPersistence: ProjectReportExpenditureCostCategoryPersistence

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
            expenditure(554L, partOfSample = true, BigDecimal.ONE, certified = BigDecimal.valueOf(9, 1)),
            expenditure(555L, partOfSample = false, null, certified = BigDecimal.valueOf(5, 1)),
        )

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, reportId = 22L) } returns costOptions
        every { reportCoFinancingPersistence.getReportCurrentSum(PARTNER_ID, reportId = 22L) } returns BigDecimal.TEN

        assertThat(interactor.get(PARTNER_ID, reportId = 22L)).isEqualTo(
            ControlWorkOverview(
                declaredByPartner = BigDecimal.TEN,
                inControlSample = BigDecimal.ONE,
                parked = BigDecimal.ZERO,
                deductedByControl = BigDecimal.valueOf(839L, 2),
                eligibleAfterControl = BigDecimal.valueOf(161L, 2),
                eligibleAfterControlPercentage = BigDecimal.valueOf(1610L, 2),
            )
        )
    }

}
