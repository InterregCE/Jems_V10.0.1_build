package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal

class GetReportControlDeductionOverviewTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 592L
        private const val REPORT_ID = 1L

        private const val TYPOLOGY_ID = 600L
        private const val TYPOLOGY_DESCRIPTION = "Typology of error"

        private const val TYPOLOGY_ID_SECOND = 601L
        private const val TYPOLOGY_DESCRIPTION_SECOND = "Typology of error 2"

        private const val TYPOLOGY_ID_THIRD = 602L
        private const val TYPOLOGY_DESCRIPTION_THIRD = "Typology of error 3"

        private val typologyOfErrors = listOf(
            TypologyErrors(id = TYPOLOGY_ID, description = TYPOLOGY_DESCRIPTION),
            TypologyErrors(id = TYPOLOGY_ID_SECOND, description = TYPOLOGY_DESCRIPTION_SECOND),
            TypologyErrors(id = TYPOLOGY_ID_THIRD, description = TYPOLOGY_DESCRIPTION_THIRD)
        )

        val costOptionsWithFlatRate = mockk<ReportExpenditureCostCategory>().also {
            every { it.options } returns ProjectPartnerBudgetOptions(
                partnerId = PARTNER_ID,
                officeAndAdministrationOnStaffCostsFlatRate = 15,
                officeAndAdministrationOnDirectCostsFlatRate = null,
                travelAndAccommodationOnStaffCostsFlatRate = 15,
                staffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
            )
            every { it.totalsFromAF.sum } returns BigDecimal.valueOf(500L)

            every { it.currentlyReported } returns BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(10),
                office = BigDecimal.valueOf(11),
                travel = BigDecimal.valueOf(12),
                external = BigDecimal.valueOf(13),
                equipment = BigDecimal.valueOf(14),
                infrastructure = BigDecimal.valueOf(15),
                other = BigDecimal.valueOf(16),
                lumpSum = BigDecimal.valueOf(17),
                unitCost = BigDecimal.valueOf(18),
                sum = BigDecimal.valueOf(19),
            )
            every { it.totalEligibleAfterControl } returns BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(9),
                office = BigDecimal.valueOf(8),
                travel = BigDecimal.valueOf(7),
                external = BigDecimal.valueOf(6),
                equipment = BigDecimal.valueOf(5),
                infrastructure = BigDecimal.valueOf(4),
                other = BigDecimal.valueOf(3),
                lumpSum = BigDecimal.valueOf(2),
                unitCost = BigDecimal.valueOf(1),
                sum = BigDecimal.valueOf(0),
            )
        }

        fun expenditure(
            id: Long,
            partOfSample: Boolean,
            declaredAmount: BigDecimal,
            deductedAmount: BigDecimal,
            certified: BigDecimal,
            isParked: Boolean,
            costCategory: ReportBudgetCategory,
            typologyOfErrorId: Long?,
            lumpSumId: Long? = null
        ): ProjectPartnerReportExpenditureVerification {
            val expenditure = mockk<ProjectPartnerReportExpenditureVerification>()
            every { expenditure.id } returns id
            every { expenditure.parked } returns isParked
            every { expenditure.partOfSample } returns partOfSample
            every { expenditure.declaredAmountAfterSubmission } returns declaredAmount
            every { expenditure.declaredAmount } returns declaredAmount
            every { expenditure.deductedAmount } returns deductedAmount
            every { expenditure.certifiedAmount } returns certified
            every { expenditure.costCategory } returns costCategory
            every { expenditure.typologyOfErrorId } returns typologyOfErrorId
            every { expenditure.unitCostId } returns null
            every { expenditure.lumpSumId } returns lumpSumId
            return expenditure
        }

        private val expenditureList = listOf(
            expenditure(
                id = 554L,
                costCategory = ReportBudgetCategory.StaffCosts,
                typologyOfErrorId = null,
                partOfSample = true,
                declaredAmount = BigDecimal.valueOf(500),
                deductedAmount = BigDecimal.ZERO,
                certified = BigDecimal.ZERO,
                isParked = true,
            ),
            expenditure(
                id = 555L,
                typologyOfErrorId = TYPOLOGY_ID_SECOND,
                costCategory = ReportBudgetCategory.StaffCosts,
                declaredAmount = BigDecimal.valueOf(2000),
                deductedAmount = BigDecimal.valueOf(350),
                certified = BigDecimal.valueOf(1650),
                partOfSample = false,
                isParked = false,
            ),
            expenditure(
                id = 556L,
                typologyOfErrorId = TYPOLOGY_ID,
                costCategory = ReportBudgetCategory.EquipmentCosts,
                declaredAmount = BigDecimal.valueOf(1111),
                deductedAmount = BigDecimal.valueOf(200),
                certified = BigDecimal.valueOf(911),
                partOfSample = false,
                isParked = false
            ),
            expenditure(
                id = 557L,
                typologyOfErrorId = TYPOLOGY_ID,
                costCategory = ReportBudgetCategory.StaffCosts,
                declaredAmount = BigDecimal.valueOf(6900),
                deductedAmount = BigDecimal.valueOf(500),
                certified = BigDecimal.valueOf(6400),
                partOfSample = false,
                isParked = false
            ),
        )

        private val expectedExpenditure_1 = ControlDeductionOverviewRow(
            typologyOfErrorId = TYPOLOGY_ID,
            typologyOfErrorName = TYPOLOGY_DESCRIPTION,
            staffCost = BigDecimal.valueOf(500L),
            officeAndAdministration = BigDecimal.ZERO,
            travelAndAccommodation = BigDecimal.ZERO,
            externalExpertise = BigDecimal.ZERO,
            equipment = BigDecimal.valueOf(200L),
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(700L)
        )
        private val expectedExpenditure_2 = ControlDeductionOverviewRow(
            typologyOfErrorId = TYPOLOGY_ID_SECOND,
            typologyOfErrorName = TYPOLOGY_DESCRIPTION_SECOND,
            staffCost = BigDecimal.valueOf(350L),
            officeAndAdministration = BigDecimal.ZERO,
            travelAndAccommodation = BigDecimal.ZERO,
            externalExpertise = BigDecimal.ZERO,
            equipment = BigDecimal.ZERO,
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(350L)
        )
        private val expectedExpenditure_3 = ControlDeductionOverviewRow(
            typologyOfErrorId = TYPOLOGY_ID_THIRD,
            typologyOfErrorName = TYPOLOGY_DESCRIPTION_THIRD,
            staffCost = BigDecimal.ZERO,
            officeAndAdministration = BigDecimal.ZERO,
            travelAndAccommodation = BigDecimal.ZERO,
            externalExpertise = BigDecimal.ZERO,
            equipment = BigDecimal.ZERO,
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.ZERO
        )

    }


    @MockK
    lateinit var reportPersistence: ProjectPartnerReportPersistence

    @MockK
    lateinit var typologyOfErrorsPersistence: ProgrammeTypologyErrorsPersistence

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence

    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence

    @InjectMockKs
    lateinit var getReportControlDeductionOverview: GetReportControlDeductionOverview

    @ParameterizedTest(name = "get Overview for deductions when flat rates are applied (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Certified"])
    fun `get Overview for deductions when flat rates are applied - closed`(status: ReportStatus) {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, REPORT_ID) } returns report

        every { reportExpenditurePersistence
            .getPartnerControlReportExpenditureVerification(PARTNER_ID, REPORT_ID)
        } returns expenditureList

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, REPORT_ID) } returns
            costOptionsWithFlatRate

        every { typologyOfErrorsPersistence.getAllTypologyErrors() } returns typologyOfErrors

        val expectedDeductionRows = listOf(
            expectedExpenditure_1,
            expectedExpenditure_2,
            expectedExpenditure_3,
            /* flat rate row */ ControlDeductionOverviewRow(
                typologyOfErrorId = null,
                typologyOfErrorName = null,
                staffCost = null,
                officeAndAdministration = BigDecimal.valueOf(3L),
                travelAndAccommodation = BigDecimal.valueOf(5L),
                externalExpertise = null,
                equipment = null,
                infrastructureAndWorks = null,
                lumpSums = null,
                unitCosts = null,
                otherCosts = null,
                total = BigDecimal.valueOf(8L)
            )
        )

        val expectedTotal =   ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(850L),
            officeAndAdministration = BigDecimal.valueOf(3L),
            travelAndAccommodation = BigDecimal.valueOf(5L),
            externalExpertise = BigDecimal.ZERO,
            equipment =  BigDecimal.valueOf(200L),
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(1058L),
        )

        assertThat(getReportControlDeductionOverview.get(PARTNER_ID, REPORT_ID)).isEqualTo(
            ControlDeductionOverview(
                deductionRows = expectedDeductionRows,
                staffCostsFlatRate = null,
                officeAndAdministrationFlatRate = 15,
                travelAndAccommodationFlatRate = 15,
                otherCostsOnStaffCostsFlatRate = null,
                total = expectedTotal,
            )
        )
    }

    @ParameterizedTest(name = "get Overview for deductions when flat rates are applied (status {0})")
    @EnumSource(value = ReportStatus::class, names = ["Certified"], mode = EnumSource.Mode.EXCLUDE)
    fun `get Overview for deductions when flat rates are applied - open`(status: ReportStatus) {
        val report = mockk<ProjectPartnerReport>()
        every { report.status } returns status
        every { reportPersistence.getPartnerReportById(PARTNER_ID, REPORT_ID) } returns report

        every { reportExpenditurePersistence
            .getPartnerControlReportExpenditureVerification(PARTNER_ID, REPORT_ID)
        } returns expenditureList

        every { reportExpenditureCostCategoryPersistence.getCostCategories(PARTNER_ID, REPORT_ID) } returns
            costOptionsWithFlatRate

        every { typologyOfErrorsPersistence.getAllTypologyErrors() } returns typologyOfErrors

        val expectedDeductionRows = listOf(
            expectedExpenditure_1,
            expectedExpenditure_2,
            expectedExpenditure_3,
            /* flat rate row */ ControlDeductionOverviewRow(
                typologyOfErrorId = null,
                typologyOfErrorName = null,
                staffCost = null,
                officeAndAdministration = BigDecimal.valueOf(-119650L, 2),
                travelAndAccommodation = BigDecimal.valueOf(-119550L, 2),
                externalExpertise = null,
                equipment = null,
                infrastructureAndWorks = null,
                lumpSums = null,
                unitCosts = null,
                otherCosts = null,
                total = BigDecimal.valueOf(-239200L, 2)
            )
        )

        val expectedTotal = ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(850L),
            officeAndAdministration = BigDecimal.valueOf(-119650L, 2),
            travelAndAccommodation = BigDecimal.valueOf(-119550L, 2),
            externalExpertise = BigDecimal.ZERO,
            equipment =  BigDecimal.valueOf(200L),
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(-134200L, 2),
        )

        assertThat(getReportControlDeductionOverview.get(PARTNER_ID, REPORT_ID)).isEqualTo(
            ControlDeductionOverview(
                deductionRows = expectedDeductionRows,
                staffCostsFlatRate = null,
                officeAndAdministrationFlatRate = 15,
                travelAndAccommodationFlatRate = 15,
                otherCostsOnStaffCostsFlatRate = null,
                total = expectedTotal,
            )
        )
    }

}
