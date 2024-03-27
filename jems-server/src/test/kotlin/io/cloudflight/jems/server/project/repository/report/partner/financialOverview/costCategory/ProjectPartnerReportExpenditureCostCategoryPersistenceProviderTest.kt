package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCurrentValuesWrapper
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryCurrentlyReportedWithReIncluded
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryPreviouslyReportedWithParked
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
            spfCostTotal = BigDecimal.valueOf(185L, 1),
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
            spfCostCurrent = BigDecimal.valueOf(285, 1),
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
            spfCostTotalEligibleAfterControl = BigDecimal.valueOf(485, 1),
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
            spfCostPreviouslyReported = BigDecimal.valueOf(385, 1),
            sumPreviouslyReported = BigDecimal.valueOf(39),

            staffPreviouslyReportedParked = BigDecimal.valueOf(50),
            officePreviouslyReportedParked = BigDecimal.valueOf(51),
            travelPreviouslyReportedParked = BigDecimal.valueOf(52),
            externalPreviouslyReportedParked = BigDecimal.valueOf(53),
            equipmentPreviouslyReportedParked = BigDecimal.valueOf(54),
            infrastructurePreviouslyReportedParked = BigDecimal.valueOf(55),
            otherPreviouslyReportedParked = BigDecimal.valueOf(56),
            lumpSumPreviouslyReportedParked = BigDecimal.valueOf(57),
            unitCostPreviouslyReportedParked = BigDecimal.valueOf(58),
            spfCostPreviouslyReportedParked = BigDecimal.valueOf(585, 1),
            sumPreviouslyReportedParked = BigDecimal.valueOf(59),

            staffCurrentParked = BigDecimal.valueOf(60),
            officeCurrentParked = BigDecimal.valueOf(61),
            travelCurrentParked = BigDecimal.valueOf(62),
            externalCurrentParked = BigDecimal.valueOf(63),
            equipmentCurrentParked = BigDecimal.valueOf(64),
            infrastructureCurrentParked = BigDecimal.valueOf(65),
            otherCurrentParked = BigDecimal.valueOf(66),
            lumpSumCurrentParked = BigDecimal.valueOf(67),
            unitCostCurrentParked = BigDecimal.valueOf(68),
            spfCostCurrentParked = BigDecimal.valueOf(685, 1),
            sumCurrentParked = BigDecimal.valueOf(69),

            staffCurrentParkedVerification = BigDecimal.valueOf(60),
            officeCurrentParkedVerification = BigDecimal.valueOf(61),
            travelCurrentParkedVerification = BigDecimal.valueOf(62),
            externalCurrentParkedVerification = BigDecimal.valueOf(63),
            equipmentCurrentParkedVerification = BigDecimal.valueOf(64),
            infrastructureCurrentParkedVerification = BigDecimal.valueOf(65),
            otherCurrentParkedVerification = BigDecimal.valueOf(66),
            lumpSumCurrentParkedVerification = BigDecimal.valueOf(67),
            unitCostCurrentParkedVerification = BigDecimal.valueOf(68),
            spfCostCurrentParkedVerification = BigDecimal.valueOf(685, 1),
            sumCurrentParkedVerification = BigDecimal.valueOf(69),

            staffCurrentReIncluded = BigDecimal.valueOf(70),
            officeCurrentReIncluded = BigDecimal.valueOf(71),
            travelCurrentReIncluded = BigDecimal.valueOf(72),
            externalCurrentReIncluded = BigDecimal.valueOf(73),
            equipmentCurrentReIncluded = BigDecimal.valueOf(74),
            infrastructureCurrentReIncluded = BigDecimal.valueOf(75),
            otherCurrentReIncluded = BigDecimal.valueOf(76),
            lumpSumCurrentReIncluded = BigDecimal.valueOf(77),
            unitCostCurrentReIncluded = BigDecimal.valueOf(78),
            spfCostCurrentReIncluded = BigDecimal.valueOf(785, 1),
            sumCurrentReIncluded = BigDecimal.valueOf(79),

            staffPreviouslyValidated = BigDecimal.valueOf(80),
            officePreviouslyValidated = BigDecimal.valueOf(81),
            travelPreviouslyValidated = BigDecimal.valueOf(82),
            externalPreviouslyValidated = BigDecimal.valueOf(83),
            equipmentPreviouslyValidated = BigDecimal.valueOf(84),
            infrastructurePreviouslyValidated = BigDecimal.valueOf(85),
            otherPreviouslyValidated = BigDecimal.valueOf(86),
            lumpSumPreviouslyValidated = BigDecimal.valueOf(87),
            unitCostPreviouslyValidated = BigDecimal.valueOf(88),
            spfCostPreviouslyValidated = BigDecimal.valueOf(885, 1),
            sumPreviouslyValidated = BigDecimal.valueOf(89),
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
                spfCost = BigDecimal.valueOf(185L, 1),
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
                spfCost = BigDecimal.valueOf(285L, 1),
                sum = BigDecimal.valueOf(29),
            ),
            currentlyReportedParked = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(60),
                office = BigDecimal.valueOf(61),
                travel = BigDecimal.valueOf(62),
                external = BigDecimal.valueOf(63),
                equipment = BigDecimal.valueOf(64),
                infrastructure = BigDecimal.valueOf(65),
                other = BigDecimal.valueOf(66),
                lumpSum = BigDecimal.valueOf(67),
                unitCost = BigDecimal.valueOf(68),
                spfCost = BigDecimal.valueOf(685L, 1),
                sum = BigDecimal.valueOf(69),
            ),
            currentlyReportedReIncluded = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(70),
                office = BigDecimal.valueOf(71),
                travel = BigDecimal.valueOf(72),
                external = BigDecimal.valueOf(73),
                equipment = BigDecimal.valueOf(74),
                infrastructure = BigDecimal.valueOf(75),
                other = BigDecimal.valueOf(76),
                lumpSum = BigDecimal.valueOf(77),
                unitCost = BigDecimal.valueOf(78),
                spfCost = BigDecimal.valueOf(785L, 1),
                sum = BigDecimal.valueOf(79),
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
                spfCost = BigDecimal.valueOf(485L, 1),
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
                spfCost = BigDecimal.valueOf(385L, 1),
                sum = BigDecimal.valueOf(39),
            ),
            previouslyValidated = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(80),
                office = BigDecimal.valueOf(81),
                travel = BigDecimal.valueOf(82),
                external = BigDecimal.valueOf(83),
                equipment = BigDecimal.valueOf(84),
                infrastructure = BigDecimal.valueOf(85),
                other = BigDecimal.valueOf(86),
                lumpSum = BigDecimal.valueOf(87),
                unitCost = BigDecimal.valueOf(88),
                spfCost = BigDecimal.valueOf(885L, 1),
                sum = BigDecimal.valueOf(89),
            ),
            previouslyReportedParked = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(50),
                office = BigDecimal.valueOf(51),
                travel = BigDecimal.valueOf(52),
                external = BigDecimal.valueOf(53),
                equipment = BigDecimal.valueOf(54),
                infrastructure = BigDecimal.valueOf(55),
                other = BigDecimal.valueOf(56),
                lumpSum = BigDecimal.valueOf(57),
                unitCost = BigDecimal.valueOf(58),
                spfCost = BigDecimal.valueOf(585L, 1),
                sum = BigDecimal.valueOf(59),
            ),
        )

        private val expenditureCurrentlyReportedWithReIncluded = ExpenditureCostCategoryCurrentlyReportedWithReIncluded(
            BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(100),
                office = BigDecimal.valueOf(101),
                travel = BigDecimal.valueOf(102),
                external = BigDecimal.valueOf(103),
                equipment = BigDecimal.valueOf(104),
                infrastructure = BigDecimal.valueOf(105),
                other = BigDecimal.valueOf(106),
                lumpSum = BigDecimal.valueOf(107),
                unitCost = BigDecimal.valueOf(108),
                spfCost = BigDecimal.valueOf(1085L, 1),
                sum = BigDecimal.valueOf(109),
            ),
            BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(110),
                office = BigDecimal.valueOf(111),
                travel = BigDecimal.valueOf(112),
                external = BigDecimal.valueOf(113),
                equipment = BigDecimal.valueOf(114),
                infrastructure = BigDecimal.valueOf(115),
                other = BigDecimal.valueOf(116),
                lumpSum = BigDecimal.valueOf(117),
                unitCost = BigDecimal.valueOf(118),
                spfCost = BigDecimal.valueOf(1185L, 1),
                sum = BigDecimal.valueOf(119),
            )
        )

        private val expenditurePreviouslyReportedWithParked = ExpenditureCostCategoryPreviouslyReportedWithParked(
            previouslyReported = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(100),
                office = BigDecimal.valueOf(101),
                travel = BigDecimal.valueOf(102),
                external = BigDecimal.valueOf(103),
                equipment = BigDecimal.valueOf(104),
                infrastructure = BigDecimal.valueOf(105),
                other = BigDecimal.valueOf(106),
                lumpSum = BigDecimal.valueOf(107),
                unitCost = BigDecimal.valueOf(108),
                spfCost = BigDecimal.valueOf(1085L, 1),
                sum = BigDecimal.valueOf(109),
            ),
            previouslyReportedParked = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(110),
                office = BigDecimal.valueOf(111),
                travel = BigDecimal.valueOf(112),
                external = BigDecimal.valueOf(113),
                equipment = BigDecimal.valueOf(114),
                infrastructure = BigDecimal.valueOf(115),
                other = BigDecimal.valueOf(116),
                lumpSum = BigDecimal.valueOf(117),
                unitCost = BigDecimal.valueOf(118),
                spfCost = BigDecimal.valueOf(1185L, 1),
                sum = BigDecimal.valueOf(119),
            ),
            previouslyValidated = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(120),
                office = BigDecimal.valueOf(121),
                travel = BigDecimal.valueOf(122),
                external = BigDecimal.valueOf(123),
                equipment = BigDecimal.valueOf(124),
                infrastructure = BigDecimal.valueOf(125),
                other = BigDecimal.valueOf(126),
                lumpSum = BigDecimal.valueOf(127),
                unitCost = BigDecimal.valueOf(128),
                spfCost = BigDecimal.valueOf(1285L, 1),
                sum = BigDecimal.valueOf(129),
            )
        )


        private val afterControl = BudgetCostsCurrentValuesWrapper(
            BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(200),
                office = BigDecimal.valueOf(201),
                travel = BigDecimal.valueOf(202),
                external = BigDecimal.valueOf(203),
                equipment = BigDecimal.valueOf(204),
                infrastructure = BigDecimal.valueOf(205),
                other = BigDecimal.valueOf(206),
                lumpSum = BigDecimal.valueOf(207),
                unitCost = BigDecimal.valueOf(208),
                spfCost = BigDecimal.valueOf(2085L, 1),
                sum = BigDecimal.valueOf(209),
            ),
            BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(210),
                office = BigDecimal.valueOf(211),
                travel = BigDecimal.valueOf(212),
                external = BigDecimal.valueOf(213),
                equipment = BigDecimal.valueOf(214),
                infrastructure = BigDecimal.valueOf(215),
                other = BigDecimal.valueOf(216),
                lumpSum = BigDecimal.valueOf(217),
                unitCost = BigDecimal.valueOf(218),
                spfCost = BigDecimal.valueOf(2185L, 1),
                sum = BigDecimal.valueOf(219),
            ),
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
        every { repository.findCumulativeForReportIds(setOf(42L, 43L)) } returns expenditurePreviouslyReportedWithParked.previouslyReported
        every { repository.findParkedCumulativeForReportIds(setOf(42L, 43L)) } returns expenditurePreviouslyReportedWithParked.previouslyReportedParked
        every { repository.findCumulativeTotalsForReportIds(setOf(42L)) } returns expenditurePreviouslyReportedWithParked.previouslyValidated
        assertThat(persistence.getCostCategoriesCumulative(setOf(42L, 43L), setOf(42L))).isEqualTo(expenditurePreviouslyReportedWithParked)
    }

    @Test
    fun updateCurrentlyReportedValues() {
        val entity = expenditureEntity()
        every { repository.findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 4L) } returns entity
        persistence.updateCurrentlyReportedValues(PARTNER_ID, reportId = 4L, expenditureCurrentlyReportedWithReIncluded)

        assertThat(entity.staffCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.staff)
        assertThat(entity.officeCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.office)
        assertThat(entity.travelCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.travel)
        assertThat(entity.externalCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.external)
        assertThat(entity.equipmentCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.equipment)
        assertThat(entity.infrastructureCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.infrastructure)
        assertThat(entity.otherCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.other)
        assertThat(entity.lumpSumCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.lumpSum)
        assertThat(entity.unitCostCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.unitCost)
        assertThat(entity.sumCurrent).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReported.sum)

        assertThat(entity.staffCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.staff)
        assertThat(entity.officeCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.office)
        assertThat(entity.travelCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.travel)
        assertThat(entity.externalCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.external)
        assertThat(entity.equipmentCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.equipment)
        assertThat(entity.infrastructureCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.infrastructure)
        assertThat(entity.otherCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.other)
        assertThat(entity.lumpSumCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.lumpSum)
        assertThat(entity.unitCostCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.unitCost)
        assertThat(entity.sumCurrentReIncluded).isEqualTo(expenditureCurrentlyReportedWithReIncluded.currentlyReportedReIncluded.sum)
    }

    @Test
    fun updateAfterControlValues() {
        val entity = expenditureEntity()
        every { repository.findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 4L) } returns entity
        persistence.updateAfterControlValues(PARTNER_ID, reportId = 4L, afterControl)

        assertThat(entity.staffTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.staff)
        assertThat(entity.officeTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.office)
        assertThat(entity.travelTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.travel)
        assertThat(entity.externalTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.external)
        assertThat(entity.equipmentTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.equipment)
        assertThat(entity.infrastructureTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.infrastructure)
        assertThat(entity.otherTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.other)
        assertThat(entity.lumpSumTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.lumpSum)
        assertThat(entity.unitCostTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.unitCost)
        assertThat(entity.sumTotalEligibleAfterControl).isEqualTo(afterControl.currentlyReported.sum)

        assertThat(entity.staffCurrentParked).isEqualTo(afterControl.currentlyReportedParked.staff)
        assertThat(entity.officeCurrentParked).isEqualTo(afterControl.currentlyReportedParked.office)
        assertThat(entity.travelCurrentParked).isEqualTo(afterControl.currentlyReportedParked.travel)
        assertThat(entity.externalCurrentParked).isEqualTo(afterControl.currentlyReportedParked.external)
        assertThat(entity.equipmentCurrentParked).isEqualTo(afterControl.currentlyReportedParked.equipment)
        assertThat(entity.infrastructureCurrentParked).isEqualTo(afterControl.currentlyReportedParked.infrastructure)
        assertThat(entity.otherCurrentParked).isEqualTo(afterControl.currentlyReportedParked.other)
        assertThat(entity.lumpSumCurrentParked).isEqualTo(afterControl.currentlyReportedParked.lumpSum)
        assertThat(entity.unitCostCurrentParked).isEqualTo(afterControl.currentlyReportedParked.unitCost)
        assertThat(entity.sumCurrentParked).isEqualTo(afterControl.currentlyReportedParked.sum)
    }

}
