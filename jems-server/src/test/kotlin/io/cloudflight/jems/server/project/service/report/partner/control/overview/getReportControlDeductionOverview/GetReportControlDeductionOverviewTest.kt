package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetReportControlDeductionOverviewTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 592L
        private const val REPORT_ID = 1L
        private const val LUMP_SUM_ID = 2L

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
        }

        val costOptionsNoFlatRate = mockk<ReportExpenditureCostCategory>().also {
            every { it.options } returns ProjectPartnerBudgetOptions(
                partnerId = PARTNER_ID,
                officeAndAdministrationOnStaffCostsFlatRate = null,
                officeAndAdministrationOnDirectCostsFlatRate = null,
                travelAndAccommodationOnStaffCostsFlatRate = null,
                staffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
            )
            every { it.totalsFromAF.sum } returns BigDecimal.ZERO
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

    }


    @MockK
    lateinit var typologyOfErrorsPersistence: ProgrammeTypologyErrorsPersistence

    @MockK
    lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence

    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence

    @InjectMockKs
    lateinit var getReportControlDeductionOverview: GetReportControlDeductionOverview

    @Test
    fun `get Overview for deductions when flat rates are applied`() {
        every { typologyOfErrorsPersistence.getAllTypologyErrors() } returns typologyOfErrors
        every {
            reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(
                PARTNER_ID,
                REPORT_ID
            )
        } returns listOf(
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

        every {
            reportExpenditureCostCategoryPersistence.getCostCategories(
                PARTNER_ID,
                REPORT_ID
            )
        } returns costOptionsWithFlatRate

        val expectedDeductionRows = mutableListOf(
            ControlDeductionOverviewRow(
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
            ),
            ControlDeductionOverviewRow(
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
            ),
            ControlDeductionOverviewRow(
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
            ),
            ControlDeductionOverviewRow(
                typologyOfErrorId = null,
                typologyOfErrorName = null,
                staffCost = BigDecimal.ZERO,
                officeAndAdministration = BigDecimal.valueOf(12750, 2),
                travelAndAccommodation = BigDecimal.valueOf(12750, 2),
                externalExpertise = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructureAndWorks = BigDecimal.ZERO,
                lumpSums = BigDecimal.ZERO,
                unitCosts = BigDecimal.ZERO,
                otherCosts = BigDecimal.ZERO,
                total = BigDecimal.valueOf(25500, 2)
            )
        )

        val expectedTotal =   ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(850L),
            officeAndAdministration = BigDecimal.valueOf(12750, 2),
            travelAndAccommodation = BigDecimal.valueOf(12750, 2),
            externalExpertise = BigDecimal.ZERO,
            equipment =  BigDecimal.valueOf(200L),
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(130500, 2)
        )

        Assertions.assertThat(getReportControlDeductionOverview.get(PARTNER_ID, REPORT_ID, "1.0")).isEqualTo(
            ControlDeductionOverview(
                deductionRows = expectedDeductionRows,
                staffCostsFlatRate = null,
                officeAndAdministrationFlatRate = 15,
                travelAndAccommodationFlatRate = 15,
                otherCostsOnStaffCostsFlatRate = null,
                total = expectedTotal
            )
        )

    }

    @Test
    fun `get Overview for deductions - no flat rates`() {
        every { typologyOfErrorsPersistence.getAllTypologyErrors() } returns typologyOfErrors
        every {
            reportExpenditurePersistence.getPartnerControlReportExpenditureVerification(
                PARTNER_ID,
                REPORT_ID
            )
        } returns listOf(
            expenditure(
                id = 554L,
                costCategory = ReportBudgetCategory.StaffCosts,
                typologyOfErrorId = TYPOLOGY_ID,
                declaredAmount = BigDecimal.valueOf(2000),
                deductedAmount = BigDecimal.valueOf(323),
                certified = BigDecimal.valueOf(1677),
                isParked = false,
                partOfSample = false,
            ),
            expenditure(
                id = 555L,
                typologyOfErrorId = TYPOLOGY_ID,
                costCategory = ReportBudgetCategory.Multiple,
                declaredAmount = BigDecimal.valueOf(5555556, 2),
                deductedAmount = BigDecimal.valueOf(5555),
                certified = BigDecimal.valueOf(5000056, 2),
                partOfSample = false,
                isParked = false,
                lumpSumId = LUMP_SUM_ID
            ),
            expenditure(
                id = 556L,
                typologyOfErrorId = null,
                costCategory = ReportBudgetCategory.TravelAndAccommodationCosts,
                declaredAmount = BigDecimal.valueOf(300),
                deductedAmount = BigDecimal.ZERO,
                certified = BigDecimal.ZERO,
                partOfSample = true,
                isParked = true
            ),
            expenditure(
                id = 557L,
                typologyOfErrorId = TYPOLOGY_ID_SECOND,
                costCategory = ReportBudgetCategory.StaffCosts,
                declaredAmount = BigDecimal.valueOf(100),
                deductedAmount = BigDecimal.valueOf(2256, 2),
                certified = BigDecimal.valueOf(7744, 2),
                partOfSample = false,
                isParked = false
            ),
            expenditure(
                id = 558L,
                typologyOfErrorId = TYPOLOGY_ID_SECOND,
                costCategory = ReportBudgetCategory.TravelAndAccommodationCosts,
                declaredAmount = BigDecimal.valueOf(150),
                deductedAmount = BigDecimal.valueOf(10020, 2),
                certified = BigDecimal.valueOf(4980, 2),
                partOfSample = false,
                isParked = false,
            ),
        )

        every {
            reportExpenditureCostCategoryPersistence.getCostCategories(
                PARTNER_ID,
                REPORT_ID
            )
        } returns costOptionsNoFlatRate


        val expectedDeductionRows = mutableListOf(
            ControlDeductionOverviewRow(
                typologyOfErrorId = TYPOLOGY_ID,
                typologyOfErrorName = TYPOLOGY_DESCRIPTION,
                staffCost = BigDecimal.valueOf(323),
                officeAndAdministration = BigDecimal.ZERO,
                travelAndAccommodation = BigDecimal.ZERO,
                externalExpertise = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructureAndWorks = BigDecimal.ZERO,
                lumpSums = BigDecimal.valueOf(5555),
                unitCosts = BigDecimal.ZERO,
                otherCosts = BigDecimal.ZERO,
                total = BigDecimal.valueOf(5878)
            ),
            ControlDeductionOverviewRow(
                typologyOfErrorId = TYPOLOGY_ID_SECOND,
                typologyOfErrorName = TYPOLOGY_DESCRIPTION_SECOND,
                staffCost = BigDecimal.valueOf(2256, 2),
                officeAndAdministration = BigDecimal.ZERO,
                travelAndAccommodation = BigDecimal.valueOf(10020, 2),
                externalExpertise = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructureAndWorks = BigDecimal.ZERO,
                lumpSums = BigDecimal.ZERO,
                unitCosts = BigDecimal.ZERO,
                otherCosts = BigDecimal.ZERO,
                total = BigDecimal.valueOf(12276, 2)
            ),
            ControlDeductionOverviewRow(
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
            ),
            ControlDeductionOverviewRow(
                typologyOfErrorId = null,
                typologyOfErrorName = null,
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
        )

        val expectedTotal =   ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(34556, 2),
            officeAndAdministration = BigDecimal.ZERO,
            travelAndAccommodation = BigDecimal.valueOf(10020, 2),
            externalExpertise = BigDecimal.ZERO,
            equipment = BigDecimal.ZERO,
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.valueOf(5555),
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(600076, 2)
        )

        Assertions.assertThat(getReportControlDeductionOverview.get(PARTNER_ID, REPORT_ID, "1.0")).isEqualTo(
            ControlDeductionOverview(
                deductionRows = expectedDeductionRows,
                staffCostsFlatRate = null,
                officeAndAdministrationFlatRate = null,
                travelAndAccommodationFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
                total = expectedTotal
            )
        )
    }
}
