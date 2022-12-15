package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ReportExpenditureCostCategory
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectPartnerReportExpenditureCostCategoryPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 362L

        private val report = mockk<ProjectPartnerReportEntity>().also {
            every { it.partnerId } returns 4L
        }

        private fun expenditureEntity() = ReportProjectPartnerExpenditureCostCategoryEntity(
            reportId = 4L,
            reportEntity = report,

            officeAndAdministrationOnStaffCostsFlatRate = 1,
            officeAndAdministrationOnDirectCostsFlatRate = 2,
            travelAndAccommodationOnStaffCostsFlatRate = 3,
            staffCostsFlatRate = 4,
            otherCostsOnStaffCostsFlatRate = 5,

            staffTotal = BigDecimal.valueOf(10),
            officeTotal = BigDecimal.valueOf(11),
            travelTotal = BigDecimal.valueOf(12),
            externalTotal = BigDecimal.valueOf(13),
            equipmentTotal = BigDecimal.valueOf(14),
            infrastructureTotal = BigDecimal.valueOf(15),
            otherTotal = BigDecimal.valueOf(16),
            lumpSumTotal = BigDecimal.valueOf(17),
            unitCostTotal = BigDecimal.valueOf(18),
            sumTotal = BigDecimal.valueOf(19),

            staffCurrent = BigDecimal.valueOf(20),
            officeCurrent = BigDecimal.valueOf(21),
            travelCurrent = BigDecimal.valueOf(22),
            externalCurrent = BigDecimal.valueOf(23),
            equipmentCurrent = BigDecimal.valueOf(24),
            infrastructureCurrent = BigDecimal.valueOf(25),
            otherCurrent = BigDecimal.valueOf(26),
            lumpSumCurrent = BigDecimal.valueOf(27),
            unitCostCurrent = BigDecimal.valueOf(28),
            sumCurrent = BigDecimal.valueOf(29),

            staffTotalEligibleAfterControl = BigDecimal.valueOf(40),
            officeTotalEligibleAfterControl = BigDecimal.valueOf(41),
            travelTotalEligibleAfterControl = BigDecimal.valueOf(42),
            externalTotalEligibleAfterControl = BigDecimal.valueOf(43),
            equipmentTotalEligibleAfterControl = BigDecimal.valueOf(44),
            infrastructureTotalEligibleAfterControl = BigDecimal.valueOf(45),
            otherTotalEligibleAfterControl = BigDecimal.valueOf(46),
            lumpSumTotalEligibleAfterControl = BigDecimal.valueOf(47),
            unitCostTotalEligibleAfterControl = BigDecimal.valueOf(48),
            sumTotalEligibleAfterControl = BigDecimal.valueOf(49),

            staffPreviouslyReported = BigDecimal.valueOf(30),
            officePreviouslyReported = BigDecimal.valueOf(31),
            travelPreviouslyReported = BigDecimal.valueOf(32),
            externalPreviouslyReported = BigDecimal.valueOf(33),
            equipmentPreviouslyReported = BigDecimal.valueOf(34),
            infrastructurePreviouslyReported = BigDecimal.valueOf(35),
            otherPreviouslyReported = BigDecimal.valueOf(36),
            lumpSumPreviouslyReported = BigDecimal.valueOf(37),
            unitCostPreviouslyReported = BigDecimal.valueOf(38),
            sumPreviouslyReported = BigDecimal.valueOf(39),
        )

        private val expenditure = ReportExpenditureCostCategory(
            options = ProjectPartnerBudgetOptions(
                partnerId = 4L,
                officeAndAdministrationOnStaffCostsFlatRate = 1,
                officeAndAdministrationOnDirectCostsFlatRate = 2,
                travelAndAccommodationOnStaffCostsFlatRate = 3,
                staffCostsFlatRate = 4,
                otherCostsOnStaffCostsFlatRate = 5,
            ),
            totalsFromAF = BudgetCostsCalculationResultFull(
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
            ),
            currentlyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(20),
                office = BigDecimal.valueOf(21),
                travel = BigDecimal.valueOf(22),
                external = BigDecimal.valueOf(23),
                equipment = BigDecimal.valueOf(24),
                infrastructure = BigDecimal.valueOf(25),
                other = BigDecimal.valueOf(26),
                lumpSum = BigDecimal.valueOf(27),
                unitCost = BigDecimal.valueOf(28),
                sum = BigDecimal.valueOf(29),
            ),
            totalEligibleAfterControl = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(40),
                office = BigDecimal.valueOf(41),
                travel = BigDecimal.valueOf(42),
                external = BigDecimal.valueOf(43),
                equipment = BigDecimal.valueOf(44),
                infrastructure = BigDecimal.valueOf(45),
                other = BigDecimal.valueOf(46),
                lumpSum = BigDecimal.valueOf(47),
                unitCost = BigDecimal.valueOf(48),
                sum = BigDecimal.valueOf(49),
            ),
            previouslyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(30),
                office = BigDecimal.valueOf(31),
                travel = BigDecimal.valueOf(32),
                external = BigDecimal.valueOf(33),
                equipment = BigDecimal.valueOf(34),
                infrastructure = BigDecimal.valueOf(35),
                other = BigDecimal.valueOf(36),
                lumpSum = BigDecimal.valueOf(37),
                unitCost = BigDecimal.valueOf(38),
                sum = BigDecimal.valueOf(39),
            ),
        )

        private val newValues = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(100),
            office = BigDecimal.valueOf(101),
            travel = BigDecimal.valueOf(102),
            external = BigDecimal.valueOf(103),
            equipment = BigDecimal.valueOf(104),
            infrastructure = BigDecimal.valueOf(105),
            other = BigDecimal.valueOf(106),
            lumpSum = BigDecimal.valueOf(107),
            unitCost = BigDecimal.valueOf(108),
            sum = BigDecimal.valueOf(109),
        )

        private val afterControl = BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(200),
            office = BigDecimal.valueOf(201),
            travel = BigDecimal.valueOf(202),
            external = BigDecimal.valueOf(203),
            equipment = BigDecimal.valueOf(204),
            infrastructure = BigDecimal.valueOf(205),
            other = BigDecimal.valueOf(206),
            lumpSum = BigDecimal.valueOf(207),
            unitCost = BigDecimal.valueOf(208),
            sum = BigDecimal.valueOf(209),
        )
    }

    @MockK
    lateinit var repository: ReportProjectPartnerExpenditureCostCategoryRepository

    @InjectMockKs
    lateinit var persistence: ProjectPartnerReportExpenditureCostCategoryPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(repository)
    }

    @Test
    fun getCostCategories() {
        every { repository.findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 4L) } returns expenditureEntity()
        assertThat(persistence.getCostCategories(PARTNER_ID, reportId = 4L)).isEqualTo(expenditure)
    }

    @Test
    fun getCostCategoriesCumulative() {
        every { repository.findCumulativeForReportIds(setOf(42L, 43L)) } returns newValues
        assertThat(persistence.getCostCategoriesCumulative(setOf(42L, 43L))).isEqualTo(newValues)
    }

    @Test
    fun updateCurrentlyReportedValues() {
        val entity = expenditureEntity()
        every { repository.findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 4L) } returns entity
        persistence.updateCurrentlyReportedValues(PARTNER_ID, reportId = 4L, newValues)

        assertThat(entity.staffCurrent).isEqualTo(newValues.staff)
        assertThat(entity.officeCurrent).isEqualTo(newValues.office)
        assertThat(entity.travelCurrent).isEqualTo(newValues.travel)
        assertThat(entity.externalCurrent).isEqualTo(newValues.external)
        assertThat(entity.equipmentCurrent).isEqualTo(newValues.equipment)
        assertThat(entity.infrastructureCurrent).isEqualTo(newValues.infrastructure)
        assertThat(entity.otherCurrent).isEqualTo(newValues.other)
        assertThat(entity.lumpSumCurrent).isEqualTo(newValues.lumpSum)
        assertThat(entity.unitCostCurrent).isEqualTo(newValues.unitCost)
        assertThat(entity.sumCurrent).isEqualTo(newValues.sum)
    }

    @Test
    fun updateAfterControlValues() {
        val entity = expenditureEntity()
        every { repository.findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 4L) } returns entity
        persistence.updateAfterControlValues(PARTNER_ID, reportId = 4L, afterControl)

        assertThat(entity.staffTotalEligibleAfterControl).isEqualTo(afterControl.staff)
        assertThat(entity.officeTotalEligibleAfterControl).isEqualTo(afterControl.office)
        assertThat(entity.travelTotalEligibleAfterControl).isEqualTo(afterControl.travel)
        assertThat(entity.externalTotalEligibleAfterControl).isEqualTo(afterControl.external)
        assertThat(entity.equipmentTotalEligibleAfterControl).isEqualTo(afterControl.equipment)
        assertThat(entity.infrastructureTotalEligibleAfterControl).isEqualTo(afterControl.infrastructure)
        assertThat(entity.otherTotalEligibleAfterControl).isEqualTo(afterControl.other)
        assertThat(entity.lumpSumTotalEligibleAfterControl).isEqualTo(afterControl.lumpSum)
        assertThat(entity.unitCostTotalEligibleAfterControl).isEqualTo(afterControl.unitCost)
        assertThat(entity.sumTotalEligibleAfterControl).isEqualTo(afterControl.sum)
    }

}
