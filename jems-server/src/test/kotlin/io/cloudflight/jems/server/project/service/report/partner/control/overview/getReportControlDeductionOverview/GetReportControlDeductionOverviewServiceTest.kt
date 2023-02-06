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

class GetReportControlDeductionOverviewServiceTest : UnitTest() {


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
                officeAndAdministrationOnStaffCostsFlatRate = 11,
                officeAndAdministrationOnDirectCostsFlatRate = null,
                travelAndAccommodationOnStaffCostsFlatRate = 14,
                staffCostsFlatRate = 20,
                otherCostsOnStaffCostsFlatRate = null,
            )
            every { it.totalsFromAF.sum } returns BigDecimal.valueOf(500L)

            every { it.currentlyReported } returns BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(153238L, 2),
                office = BigDecimal.valueOf(16856L, 2),
                travel = BigDecimal.valueOf(21453L, 2),
                external = BigDecimal.valueOf(287891L, 2),
                equipment = BigDecimal.valueOf(358300L, 2),
                infrastructure = BigDecimal.valueOf(120000L, 2),
                other = BigDecimal.valueOf(0L, 2),
                lumpSum = BigDecimal.valueOf(0L, 2),
                unitCost = BigDecimal.valueOf(0L, 2),
                sum = BigDecimal.valueOf(957738L, 2),
            )

            // used when call open
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
                sum = BigDecimal.valueOf(45),
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
                id = 553L,
                costCategory = ReportBudgetCategory.ExternalCosts,
                typologyOfErrorId = null,
                partOfSample = false,
                declaredAmount = BigDecimal.valueOf(50000L, 2),
                deductedAmount = BigDecimal.ZERO,
                certified = BigDecimal.valueOf(50000L, 2),
                isParked = false,
            ),
            expenditure(
                id = 554L,
                costCategory = ReportBudgetCategory.ExternalCosts,
                typologyOfErrorId = null,
                partOfSample = true,
                declaredAmount = BigDecimal.valueOf(237891L, 2),
                deductedAmount = BigDecimal.ZERO,
                certified = BigDecimal.ZERO,
                isParked = true,
            ),
            expenditure(
                id = 555L,
                typologyOfErrorId = TYPOLOGY_ID_SECOND,
                costCategory = ReportBudgetCategory.EquipmentCosts,
                declaredAmount = BigDecimal.valueOf(234900L, 2),
                deductedAmount = BigDecimal.valueOf(100011L, 2),
                certified = BigDecimal.valueOf(134889L, 2),
                partOfSample = true,
                isParked = false,
            ),
            expenditure(
                id = 556L,
                typologyOfErrorId = null,
                costCategory = ReportBudgetCategory.EquipmentCosts,
                declaredAmount = BigDecimal.valueOf(123400L, 2),
                deductedAmount = BigDecimal.ZERO,
                certified = BigDecimal.valueOf(123400L, 2),
                partOfSample = false,
                isParked = false
            ),
            expenditure(
                id = 557L,
                typologyOfErrorId = null,
                costCategory = ReportBudgetCategory.InfrastructureCosts,
                declaredAmount = BigDecimal.valueOf(120000L, 2),
                deductedAmount = BigDecimal.ZERO,
                certified = BigDecimal.valueOf(120000L, 2),
                partOfSample = false,
                isParked = false
            ),
        )

        private val expectedTypo_1 = ControlDeductionOverviewRow(
            typologyOfErrorId = TYPOLOGY_ID,
            typologyOfErrorName = TYPOLOGY_DESCRIPTION,
            staffCost = BigDecimal.ZERO,
            officeAndAdministration = BigDecimal.ZERO,
            travelAndAccommodation = BigDecimal.ZERO,
            externalExpertise = BigDecimal.ZERO,
            equipment = BigDecimal.ZERO,
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.ZERO,
        )
        private val expectedTypo_2 = ControlDeductionOverviewRow(
            typologyOfErrorId = TYPOLOGY_ID_SECOND,
            typologyOfErrorName = TYPOLOGY_DESCRIPTION_SECOND,
            staffCost = BigDecimal.ZERO,
            officeAndAdministration = BigDecimal.ZERO,
            travelAndAccommodation = BigDecimal.ZERO,
            externalExpertise = BigDecimal.ZERO,
            equipment = BigDecimal.valueOf(100011L, 2),
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(100011L, 2),
        )
        private val expectedTypo_3 = ControlDeductionOverviewRow(
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
            total = BigDecimal.ZERO,
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
    lateinit var getReportControlDeductionOverviewService: GetReportControlDeductionOverviewService

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
            expectedTypo_1,
            expectedTypo_2,
            expectedTypo_3,
            /* flat rate row */ ControlDeductionOverviewRow(
                typologyOfErrorId = null,
                typologyOfErrorName = null,
                staffCost = BigDecimal.valueOf(104760L, 2),
                officeAndAdministration = BigDecimal.valueOf(10823L, 2),
                travelAndAccommodation = BigDecimal.valueOf(14093L, 2),
                externalExpertise = null,
                equipment = null,
                infrastructureAndWorks = null,
                lumpSums = null,
                unitCosts = null,
                otherCosts = null,
                total = BigDecimal.valueOf(129676L, 2),
            )
        )

        val expectedTotal =   ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(104760L, 2),
            officeAndAdministration = BigDecimal.valueOf(10823L, 2),
            travelAndAccommodation = BigDecimal.valueOf(14093L, 2),
            externalExpertise = BigDecimal.ZERO,
            equipment =  BigDecimal.valueOf(100011L, 2),
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(229687L, 2),
        )

        assertThat(getReportControlDeductionOverviewService.get(PARTNER_ID, REPORT_ID)).isEqualTo(
            ControlDeductionOverview(
                deductionRows = expectedDeductionRows,
                staffCostsFlatRate = 20,
                officeAndAdministrationFlatRate = 11,
                travelAndAccommodationFlatRate = 14,
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
            expectedTypo_1,
            expectedTypo_2,
            expectedTypo_3,
            /* flat rate row */ ControlDeductionOverviewRow(
                typologyOfErrorId = null,
                typologyOfErrorName = null,
                staffCost = BigDecimal.valueOf(20003L, 2),
                officeAndAdministration = BigDecimal.valueOf(2201L, 2),
                travelAndAccommodation = BigDecimal.valueOf(2802L, 2),
                externalExpertise = null,
                equipment = null,
                infrastructureAndWorks = null,
                lumpSums = null,
                unitCosts = null,
                otherCosts = null,
                total = BigDecimal.valueOf(25006L, 2)
            )
        )

        val expectedTotal = ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(20003L, 2),
            officeAndAdministration = BigDecimal.valueOf(2201L, 2),
            travelAndAccommodation = BigDecimal.valueOf(2802L, 2),
            externalExpertise = BigDecimal.ZERO,
            equipment =  BigDecimal.valueOf(100011L, 2),
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(125017L, 2),
        )

        assertThat(getReportControlDeductionOverviewService.get(PARTNER_ID, REPORT_ID)).isEqualTo(
            ControlDeductionOverview(
                deductionRows = expectedDeductionRows,
                staffCostsFlatRate = 20,
                officeAndAdministrationFlatRate = 11,
                travelAndAccommodationFlatRate = 14,
                otherCostsOnStaffCostsFlatRate = null,
                total = expectedTotal,
            )
        )
    }


}