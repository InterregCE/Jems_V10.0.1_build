package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getDeductionOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.repository.report.project.verification.ProjectReportVerificationExpenditurePersistenceProvider
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectPartnerReportExpenditureItem
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.VerificationDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportVerificationDeductionOverviewCalculatorTest: UnitTest() {

    companion object {
        const val PROJECT_REPORT_ID = 90L

        private const val LEAD_PARTNER_ID = 47L
        private const val SECOND_PARTNER_ID = 48L

        const val LEAD_PARTNER_R1_ID = 9L
        const val SECOND_PARTNER_R1_ID = 15L

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

        // LEAD PARTNER Certificate verification expenditures
        val costOptionsWithFlatRate = mockk<ReportExpenditureCostCategory>().also {
            every { it.options } returns ProjectPartnerBudgetOptions(
                partnerId = LEAD_PARTNER_ID,
                officeAndAdministrationOnStaffCostsFlatRate = null,
                officeAndAdministrationOnDirectCostsFlatRate = null,
                travelAndAccommodationOnStaffCostsFlatRate = 10,
                staffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
            )

            every { it.totalEligibleAfterControl } returns BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(3550.50),
                office = BigDecimal.valueOf(0),
                travel = BigDecimal.valueOf(1000.00),
                external = BigDecimal.valueOf(0),
                equipment = BigDecimal.valueOf(300.50),
                infrastructure = BigDecimal.valueOf(0),
                other = BigDecimal.valueOf(0),
                lumpSum = BigDecimal.valueOf(0),
                unitCost = BigDecimal.valueOf(0),
                sum = BigDecimal.valueOf(4250),
            )
        }

        val leadPartnerVerificationExpenditures = listOf(
            getVerificationExpenditureLineMock(
                expenditureItem = getExpenditureItemMock(LEAD_PARTNER_ID, ProjectPartnerRole.LEAD_PARTNER, 1,
                    partnerReportId = LEAD_PARTNER_R1_ID,
                    partnerReportNumber = 1,
                    reportCostCategory = ReportBudgetCategory.StaffCosts,
                    budgetCostCategory = BudgetCostCategory.Staff,
                    certifiedAmount = BigDecimal.valueOf(2187.34)
                ),
                deductedByJs = BigDecimal.valueOf(100.70),
                deductedByMa = BigDecimal.valueOf(21.00),
                amountAfterVerification = BigDecimal.valueOf(2065.64),
                typologyOfErrorId = TYPOLOGY_ID,
                budgetCostCategory = BudgetCostCategory.Staff,
                parked = false
            ),
            getVerificationExpenditureLineMock(
                expenditureItem = getExpenditureItemMock(LEAD_PARTNER_ID, ProjectPartnerRole.LEAD_PARTNER, 1,
                    partnerReportId = LEAD_PARTNER_R1_ID,
                    partnerReportNumber = 1,
                    reportCostCategory = ReportBudgetCategory.StaffCosts,
                    budgetCostCategory = BudgetCostCategory.Staff,
                    certifiedAmount = BigDecimal.valueOf(1062.66)
                ),
                deductedByJs = BigDecimal.valueOf(219.01),
                deductedByMa = BigDecimal.valueOf(0),
                amountAfterVerification = BigDecimal.valueOf(843.65),
                typologyOfErrorId = TYPOLOGY_ID,
                budgetCostCategory = BudgetCostCategory.Staff,
                parked = false
            ),
            getVerificationExpenditureLineMock(
                expenditureItem = getExpenditureItemMock(LEAD_PARTNER_ID, ProjectPartnerRole.LEAD_PARTNER, 1,
                    partnerReportId = LEAD_PARTNER_R1_ID,
                    partnerReportNumber = 1,
                    reportCostCategory = ReportBudgetCategory.TravelAndAccommodationCosts,
                    budgetCostCategory = BudgetCostCategory.Travel,
                    certifiedAmount = BigDecimal.valueOf(1000.00)
                ),
                deductedByJs = BigDecimal.valueOf(0),
                deductedByMa = BigDecimal.valueOf(55.00),
                amountAfterVerification = BigDecimal.valueOf(945.00),
                typologyOfErrorId = TYPOLOGY_ID_THIRD,
                budgetCostCategory = BudgetCostCategory.Travel,
                parked = false
            ),
            getVerificationExpenditureLineMock(
                expenditureItem = getExpenditureItemMock(LEAD_PARTNER_ID, ProjectPartnerRole.LEAD_PARTNER, 1,
                    partnerReportId = LEAD_PARTNER_R1_ID,
                    partnerReportNumber = 1,
                    reportCostCategory = ReportBudgetCategory.StaffCosts,
                    budgetCostCategory = BudgetCostCategory.Staff,
                    certifiedAmount = BigDecimal.valueOf(300.50)
                ),
                deductedByJs = BigDecimal.valueOf(0),
                deductedByMa = BigDecimal.valueOf(0),
                amountAfterVerification = BigDecimal.valueOf(0),
                typologyOfErrorId = null,
                budgetCostCategory = BudgetCostCategory.Staff,
                parked = true
            ),
            getVerificationExpenditureLineMock(
                expenditureItem = getExpenditureItemMock(LEAD_PARTNER_ID, ProjectPartnerRole.LEAD_PARTNER, 1,
                    partnerReportId = LEAD_PARTNER_R1_ID,
                    partnerReportNumber = 1,
                    reportCostCategory = ReportBudgetCategory.EquipmentCosts,
                    budgetCostCategory = BudgetCostCategory.Equipment,
                    certifiedAmount = BigDecimal.valueOf(1000)
                ),
                deductedByJs = BigDecimal.valueOf(500.00),
                deductedByMa = BigDecimal.valueOf(0),
                amountAfterVerification = BigDecimal.valueOf(0),
                typologyOfErrorId = TYPOLOGY_ID,
                budgetCostCategory = BudgetCostCategory.Equipment,
                parked = false
            )
        )


        // Second Partner data

        val partner2_costOptionsWithFlatRate = mockk<ReportExpenditureCostCategory>().also {
            every { it.options } returns ProjectPartnerBudgetOptions(
                partnerId = SECOND_PARTNER_ID,
                officeAndAdministrationOnStaffCostsFlatRate = null,
                officeAndAdministrationOnDirectCostsFlatRate = null,
                travelAndAccommodationOnStaffCostsFlatRate = null,
                staffCostsFlatRate = null,
                otherCostsOnStaffCostsFlatRate = null,
            )
            every { it.totalsFromAF.sum } returns BigDecimal.valueOf(3000)

            every { it.totalEligibleAfterControl } returns BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(0),
                office = BigDecimal.valueOf(0),
                travel = BigDecimal.valueOf(0),
                external = BigDecimal.valueOf(0),
                equipment = BigDecimal.valueOf(2000.00),
                infrastructure = BigDecimal.valueOf(500.00),
                other = BigDecimal.valueOf(0),
                lumpSum = BigDecimal.valueOf(0),
                unitCost = BigDecimal.valueOf(0),
                sum = BigDecimal.valueOf(2500.00),
            )
        }


        val secondPartnerExpenditures = listOf(
            getVerificationExpenditureLineMock(
                expenditureItem = getExpenditureItemMock(SECOND_PARTNER_ID, ProjectPartnerRole.PARTNER, 2,
                    partnerReportId = SECOND_PARTNER_R1_ID,
                    partnerReportNumber = 1,
                    reportCostCategory = ReportBudgetCategory.EquipmentCosts,
                    budgetCostCategory = BudgetCostCategory.Equipment,
                    certifiedAmount = BigDecimal.valueOf(2000)
                ),
                deductedByJs = BigDecimal.valueOf(99.01),
                deductedByMa = BigDecimal.valueOf(765.00),
                amountAfterVerification = BigDecimal.valueOf(1135.99),
                typologyOfErrorId = TYPOLOGY_ID_SECOND,
                budgetCostCategory = BudgetCostCategory.Equipment,
                parked = false
            ),
            getVerificationExpenditureLineMock(
                expenditureItem = getExpenditureItemMock( SECOND_PARTNER_ID, ProjectPartnerRole.PARTNER, 2,
                    partnerReportId = SECOND_PARTNER_R1_ID,
                    partnerReportNumber = 1,
                    reportCostCategory = ReportBudgetCategory.InfrastructureCosts,
                    budgetCostCategory = BudgetCostCategory.Infrastructure,
                    certifiedAmount = BigDecimal.valueOf(500)
                ),
                deductedByJs = BigDecimal.valueOf(125.5),
                deductedByMa = BigDecimal.valueOf(0),
                amountAfterVerification = BigDecimal.valueOf(374.5),
                typologyOfErrorId = TYPOLOGY_ID_SECOND,
                budgetCostCategory = BudgetCostCategory.Infrastructure,
                parked = false
            )
        )


        // Helper functions

        private fun getVerificationExpenditureLineMock(
            expenditureItem: ProjectPartnerReportExpenditureItem,
            deductedByJs: BigDecimal,
            deductedByMa: BigDecimal,
            amountAfterVerification: BigDecimal,
            typologyOfErrorId: Long?,
            budgetCostCategory: BudgetCostCategory,
            parked: Boolean
        ): ProjectReportVerificationExpenditureLine {
            val costCategoryMock = mockk<ProjectReportVerificationExpenditureLine>()
            every { costCategoryMock.expenditure } returns expenditureItem
            every { costCategoryMock.deductedByJs } returns deductedByJs
            every { costCategoryMock.deductedByMa } returns deductedByMa
            every { costCategoryMock.amountAfterVerification } returns amountAfterVerification
            every { costCategoryMock.parked } returns parked
            every { costCategoryMock.typologyOfErrorId } returns typologyOfErrorId
            every { costCategoryMock.partOfVerificationSample } returns false

            every { costCategoryMock.getCategory() } returns budgetCostCategory
            return  costCategoryMock
        }

        private fun getExpenditureItemMock(
            partnerId: Long,
            partnerRole: ProjectPartnerRole,
            partnerNumber: Int,
            partnerReportId: Long,
            partnerReportNumber: Int,
            reportCostCategory: ReportBudgetCategory,
            budgetCostCategory: BudgetCostCategory,
            certifiedAmount: BigDecimal
        ): ProjectPartnerReportExpenditureItem {
            val expenditureItemMock = mockk<ProjectPartnerReportExpenditureItem>()

            every { expenditureItemMock.id } returns 1L
            every { expenditureItemMock.partnerId } returns partnerId
            every { expenditureItemMock.partnerRole } returns partnerRole
            every { expenditureItemMock.partnerNumber } returns partnerNumber
            every { expenditureItemMock.partnerReportId } returns partnerReportId
            every { expenditureItemMock.partnerReportNumber } returns partnerReportNumber
            every { expenditureItemMock.certifiedAmount } returns certifiedAmount
            every { expenditureItemMock.parkingMetadata } returns mockk<ExpenditureParkingMetadata>()
            every { expenditureItemMock.declaredAmountAfterSubmission } returns null
            every { expenditureItemMock.lumpSum } returns null

            every { expenditureItemMock.costCategory } returns reportCostCategory
            every { expenditureItemMock.getCategory() } returns budgetCostCategory

            return expenditureItemMock
        }

    }

    @MockK
    lateinit var projectReportVerificationExpenditurePersistenceProvider: ProjectReportVerificationExpenditurePersistenceProvider
    @MockK
    lateinit var typologyOfErrorsPersistence: ProgrammeTypologyErrorsPersistence
    @MockK
    lateinit var reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence

    @InjectMockKs
    lateinit var projectReportVerificationDeductionOverviewCalculator: ProjectReportVerificationDeductionOverviewCalculator


    @Test
    fun getDeductionOverview() {

        every { typologyOfErrorsPersistence.getAllTypologyErrors() } returns typologyOfErrors

        every { reportExpenditureCostCategoryPersistence.getCostCategoriesFor(setOf(LEAD_PARTNER_R1_ID, SECOND_PARTNER_R1_ID)) } returns mapOf(
            LEAD_PARTNER_R1_ID to costOptionsWithFlatRate,
            SECOND_PARTNER_R1_ID to partner2_costOptionsWithFlatRate
        )

        every {
            projectReportVerificationExpenditurePersistenceProvider.getProjectReportExpenditureVerification(
                PROJECT_REPORT_ID
            )
        } returns buildList {
            addAll(leadPartnerVerificationExpenditures)
            addAll(secondPartnerExpenditures)
        }.toList()

       val deductions = projectReportVerificationDeductionOverviewCalculator.getDeductionOverview(PROJECT_REPORT_ID)

        assertThat(deductions.size).isEqualTo(2)
        val leadPartnerCertificateDeductions = deductions[0]
        assertThat(leadPartnerCertificateDeductions.deductionOverview.deductionRows.size).isEqualTo(3)

        assertThat(leadPartnerCertificateDeductions.deductionOverview.deductionRows).containsExactly(
            VerificationDeductionOverviewRow(
                typologyOfErrorId = 600,
                typologyOfErrorName = "Typology of error",
                staffCost = BigDecimal.valueOf(340.71),
                officeAndAdministration = BigDecimal.valueOf(0),
                travelAndAccommodation = BigDecimal.valueOf(0),
                externalExpertise = BigDecimal.valueOf(0),
                equipment = BigDecimal.valueOf(1000),
                infrastructureAndWorks = BigDecimal.valueOf(0),
                lumpSums = BigDecimal.valueOf(0),
                unitCosts = BigDecimal.valueOf(0),
                otherCosts = BigDecimal.valueOf(0),
                total = BigDecimal.valueOf(1340.71)
            ),
            VerificationDeductionOverviewRow(
                typologyOfErrorId = 602,
                typologyOfErrorName = "Typology of error 3",
                staffCost = BigDecimal.valueOf(0),
                officeAndAdministration = BigDecimal.valueOf(0),
                travelAndAccommodation = BigDecimal.valueOf(55.0),
                externalExpertise = BigDecimal.valueOf(0),
                equipment = BigDecimal.valueOf(0),
                infrastructureAndWorks = BigDecimal.valueOf(0),
                lumpSums = BigDecimal.valueOf(0),
                unitCosts = BigDecimal.valueOf(0),
                otherCosts = BigDecimal.valueOf(0),
                total = BigDecimal.valueOf(55.0)
            ),
            VerificationDeductionOverviewRow(
                typologyOfErrorId = null,
                typologyOfErrorName = null,
                staffCost = BigDecimal.ZERO,
                officeAndAdministration = BigDecimal.ZERO,
                travelAndAccommodation = BigDecimal.valueOf(679.03),
                externalExpertise = BigDecimal.ZERO,
                equipment = BigDecimal.ZERO,
                infrastructureAndWorks = BigDecimal.ZERO,
                lumpSums = BigDecimal.ZERO,
                unitCosts = BigDecimal.ZERO,
                otherCosts = BigDecimal.ZERO,
                total = BigDecimal.valueOf(679.03)
            )
        )

        // LP TOTAL
        assertThat(leadPartnerCertificateDeductions.deductionOverview.total).isEqualTo(

            VerificationDeductionOverviewRow(
                typologyOfErrorId = null,
                typologyOfErrorName = null,
                staffCost = BigDecimal.valueOf(340.71),
                officeAndAdministration = BigDecimal.valueOf(0),
                travelAndAccommodation = BigDecimal.valueOf(734.03),
                externalExpertise = BigDecimal.valueOf(0),
                equipment = BigDecimal.valueOf(1000),
                infrastructureAndWorks = BigDecimal.valueOf(0),
                lumpSums = BigDecimal.valueOf(0),
                unitCosts = BigDecimal.valueOf(0),
                otherCosts = BigDecimal.valueOf(0),
                total = BigDecimal.valueOf(2074.74)
            )
        )


        val secondPartnerCertificateDeductions = deductions[1]
        assertThat(secondPartnerCertificateDeductions.deductionOverview.deductionRows.size).isEqualTo(1)

        assertThat(secondPartnerCertificateDeductions.deductionOverview.deductionRows).containsExactly(
            VerificationDeductionOverviewRow(
                typologyOfErrorId = 601,
                typologyOfErrorName = "Typology of error 2",
                staffCost = BigDecimal.valueOf(0),
                officeAndAdministration = BigDecimal.valueOf(0),
                travelAndAccommodation = BigDecimal.valueOf(0),
                externalExpertise = BigDecimal.valueOf(0),
                equipment = BigDecimal.valueOf(864.01),
                infrastructureAndWorks = BigDecimal.valueOf(125.5),
                lumpSums = BigDecimal.valueOf(0),
                unitCosts = BigDecimal.valueOf(0),
                otherCosts = BigDecimal.valueOf(0),
                total = BigDecimal.valueOf(989.51)
            )
        )


        // SECOND PARTNER TOTAL
        assertThat(secondPartnerCertificateDeductions.deductionOverview.total).isEqualTo(
            VerificationDeductionOverviewRow(
                typologyOfErrorId = null,
                typologyOfErrorName = null,
                staffCost = BigDecimal.valueOf(0),
                officeAndAdministration = BigDecimal.valueOf(0),
                travelAndAccommodation = BigDecimal.valueOf(0),
                externalExpertise = BigDecimal.valueOf(0),
                equipment = BigDecimal.valueOf(864.01),
                infrastructureAndWorks = BigDecimal.valueOf(125.5),
                lumpSums = BigDecimal.valueOf(0),
                unitCosts = BigDecimal.valueOf(0),
                otherCosts = BigDecimal.valueOf(0),
                total = BigDecimal.valueOf(989.51)
            )
        )




    }

}