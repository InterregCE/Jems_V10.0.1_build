package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
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
import java.time.LocalDate

class GetReportControlDeductionOverviewCalculatorTest : UnitTest() {


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
                spfCost = BigDecimal.valueOf(15000L, 2),
                sum = BigDecimal.valueOf(972738L, 2),
            )

            every { it.currentlyReportedParked } returns BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(110051L, 2),
                office = BigDecimal.valueOf(11023L, 2),
                travel = BigDecimal.valueOf(12002L, 2),
                external = BigDecimal.valueOf(142015L, 2),
                equipment = BigDecimal.valueOf(50000L, 2),
                infrastructure = BigDecimal.valueOf(110000L, 2),
                other = BigDecimal.valueOf(110L, 2),
                lumpSum = BigDecimal.valueOf(120L, 2),
                unitCost = BigDecimal.valueOf(130L, 2),
                spfCost = BigDecimal.valueOf(140L, 2),
                sum = BigDecimal.valueOf(435591L, 2),
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
                spfCost = BigDecimal.valueOf(10),
                sum = BigDecimal.valueOf(55),
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
            lumpSumId: Long? = null,
        ) = ProjectPartnerReportExpenditureVerification(
            id = id,
            number = -1,
            lumpSumId = lumpSumId,
            unitCostId = null,
            costCategory = costCategory,
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
            declaredAmount = declaredAmount,
            currencyCode = "",
            currencyConversionRate = mockk(),
            declaredAmountAfterSubmission = declaredAmount,
            attachment = mockk(),
            partOfSample = partOfSample,
            certifiedAmount = certified,
            deductedAmount = deductedAmount,
            typologyOfErrorId = typologyOfErrorId,
            verificationComment = "comment dummy",
            parked = isParked,
            parkedOn = null,
            parkingMetadata = null,
            partOfSampleLocked = false,
        )

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
    lateinit var getReportControlDeductionOverviewCalculator: GetReportControlDeductionOverviewCalculator

    @ParameterizedTest(name = "get Overview for deductions when flat rates are applied - closed (status {0})")
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
                staffCost = BigDecimal.valueOf(42287L, 2),
                officeAndAdministration = BigDecimal.valueOf(5033L, 2),
                travelAndAccommodation = BigDecimal.valueOf(8751L, 2),
                externalExpertise = null,
                equipment = null,
                infrastructureAndWorks = null,
                lumpSums = null,
                unitCosts = null,
                otherCosts = null,
                total = BigDecimal.valueOf(56071L, 2),
            )
        )

        val expectedTotal =   ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = BigDecimal.valueOf(42287L, 2),
            officeAndAdministration = BigDecimal.valueOf(5033L, 2),
            travelAndAccommodation = BigDecimal.valueOf(8751L, 2),
            externalExpertise = BigDecimal.ZERO,
            equipment =  BigDecimal.valueOf(100011L, 2),
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = BigDecimal.ZERO,
            total = BigDecimal.valueOf(156082L, 2),
        )

        assertThat(getReportControlDeductionOverviewCalculator.get(PARTNER_ID, REPORT_ID)).isEqualTo(
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

    @ParameterizedTest(name = "get Overview for deductions when flat rates are applied - open (status {0})")
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

        assertThat(getReportControlDeductionOverviewCalculator.get(PARTNER_ID, REPORT_ID)).isEqualTo(
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
