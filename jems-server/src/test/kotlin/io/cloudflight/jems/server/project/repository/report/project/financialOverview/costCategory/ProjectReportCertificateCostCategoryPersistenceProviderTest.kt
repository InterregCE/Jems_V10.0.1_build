package io.cloudflight.jems.server.project.repository.report.project.financialOverview.costCategory

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCostCategoryEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileId
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.costCategory.ReportProjectPartnerExpenditureCostCategoryRepository
import io.cloudflight.jems.server.project.repository.report.partner.model.PerPartnerCertificateCostCategory
import io.cloudflight.jems.server.project.repository.report.project.identification.ProjectReportSpendingProfileRepository
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.budget.model.ExpenditureCostCategoryCurrentlyReportedWithReIncluded
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryCurrentlyReported
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryPrevious
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryPreviouslyReported
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportCertificateCostCategoryPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 362L

        private val report = mockk<ProjectReportEntity>().also {
            every { it.projectId } returns PROJECT_ID
        }

        private fun expenditureEntity() = ReportProjectCertificateCostCategoryEntity(
            reportId = 4L,
            reportEntity = report,

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
            sumCurrent = BigDecimal.valueOf(29),

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

            staffCurrentVerified = BigDecimal.valueOf(40),
            officeCurrentVerified = BigDecimal.valueOf(41),
            travelCurrentVerified = BigDecimal.valueOf(42),
            externalCurrentVerified = BigDecimal.valueOf(43),
            equipmentCurrentVerified = BigDecimal.valueOf(44),
            infrastructureCurrentVerified = BigDecimal.valueOf(45),
            otherCurrentVerified = BigDecimal.valueOf(46),
            lumpSumCurrentVerified = BigDecimal.valueOf(47),
            unitCostCurrentVerified = BigDecimal.valueOf(48),
            sumCurrentVerified = BigDecimal.valueOf(49),

            staffPreviouslyVerified = BigDecimal.valueOf(50),
            officePreviouslyVerified = BigDecimal.valueOf(51),
            travelPreviouslyVerified = BigDecimal.valueOf(52),
            externalPreviouslyVerified = BigDecimal.valueOf(53),
            equipmentPreviouslyVerified = BigDecimal.valueOf(54),
            infrastructurePreviouslyVerified = BigDecimal.valueOf(55),
            otherPreviouslyVerified = BigDecimal.valueOf(56),
            lumpSumPreviouslyVerified = BigDecimal.valueOf(57),
            unitCostPreviouslyVerified = BigDecimal.valueOf(58),
            sumPreviouslyVerified = BigDecimal.valueOf(59),
        )

        private val expenditure = ReportCertificateCostCategory(
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
                spfCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(29),
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
                spfCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(39),
            ),
            currentVerified = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(40),
                office = BigDecimal.valueOf(41),
                travel = BigDecimal.valueOf(42),
                external = BigDecimal.valueOf(43),
                equipment = BigDecimal.valueOf(44),
                infrastructure = BigDecimal.valueOf(45),
                other = BigDecimal.valueOf(46),
                lumpSum = BigDecimal.valueOf(47),
                unitCost = BigDecimal.valueOf(48),
                spfCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(49),
            ),
            previouslyVerified = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(50),
                office = BigDecimal.valueOf(51),
                travel = BigDecimal.valueOf(52),
                external = BigDecimal.valueOf(53),
                equipment = BigDecimal.valueOf(54),
                infrastructure = BigDecimal.valueOf(55),
                other = BigDecimal.valueOf(56),
                lumpSum = BigDecimal.valueOf(57),
                unitCost = BigDecimal.valueOf(58),
                spfCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(59),
            )

        )

        private val expenditureCurrentlyReportedWithReIncluded = ExpenditureCostCategoryCurrentlyReportedWithReIncluded(
            currentlyReported = BudgetCostsCalculationResultFull(
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
            currentlyReportedReIncluded = BudgetCostsCalculationResultFull(
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

        private val expenditurePreviouslyReportedWithParked = CertificateCostCategoryPreviouslyReported(
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
        )

        private val expected = CertificateCostCategoryPrevious(
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
            previouslyVerified = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(210),
                office = BigDecimal.valueOf(211),
                travel = BigDecimal.valueOf(212),
                external = BigDecimal.valueOf(213),
                equipment = BigDecimal.valueOf(214),
                infrastructure = BigDecimal.valueOf(215),
                other = BigDecimal.valueOf(216),
                lumpSum = BigDecimal.valueOf(217),
                unitCost = BigDecimal.valueOf(218),
                spfCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(219),
            )
        )

        private val expendituresPreviouslyVerified =  BudgetCostsCalculationResultFull(
            staff = BigDecimal.valueOf(210),
            office = BigDecimal.valueOf(211),
            travel = BigDecimal.valueOf(212),
            external = BigDecimal.valueOf(213),
            equipment = BigDecimal.valueOf(214),
            infrastructure = BigDecimal.valueOf(215),
            other = BigDecimal.valueOf(216),
            lumpSum = BigDecimal.valueOf(217),
            unitCost = BigDecimal.valueOf(218),
            spfCost = BigDecimal.ZERO,
            sum = BigDecimal.valueOf(219),
        )

        private fun partnerProfile() = ProjectReportSpendingProfileEntity(
            ProjectReportSpendingProfileId(mockk(), 45L),
            partnerNumber = 4,
            partnerAbbreviation = "abbr-4",
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            country = "my country",
            previouslyReported = BigDecimal.TEN,
            currentlyReported = BigDecimal.ONE,
        )

        private fun perPartnerSum() = PerPartnerCertificateCostCategory(
            partnerId = 45L,
            officeAndAdministrationOnStaffCostsFlatRate = null,
            officeAndAdministrationOnDirectCostsFlatRate = 12,
            travelAndAccommodationOnStaffCostsFlatRate = 18,
            staffCostsFlatRate = 79,
            otherCostsOnStaffCostsFlatRate = 24,
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
            staffDeduction = BigDecimal.valueOf(30),
            officeDeduction = BigDecimal.valueOf(31),
            travelDeduction = BigDecimal.valueOf(32),
            externalDeduction = BigDecimal.valueOf(33),
            equipmentDeduction = BigDecimal.valueOf(34),
            infrastructureDeduction = BigDecimal.valueOf(35),
            otherDeduction = BigDecimal.valueOf(36),
            lumpSumDeduction = BigDecimal.valueOf(37),
            unitCostDeduction = BigDecimal.valueOf(38),
            sumDeduction = BigDecimal.valueOf(39),
        )
        private val expectedPerPartner = PerPartnerCostCategoryBreakdownLine(
            partnerId = 45L,
            partnerNumber = 4,
            partnerAbbreviation = "abbr-4",
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            country = "my country",
            officeAndAdministrationOnStaffCostsFlatRate = null,
            officeAndAdministrationOnDirectCostsFlatRate = 12,
            travelAndAccommodationOnStaffCostsFlatRate = 18,
            staffCostsFlatRate = 79,
            otherCostsOnStaffCostsFlatRate = 24,
            current = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(20),
                office = BigDecimal.valueOf(21),
                travel = BigDecimal.valueOf(22),
                external = BigDecimal.valueOf(23),
                equipment = BigDecimal.valueOf(24),
                infrastructure = BigDecimal.valueOf(25),
                other = BigDecimal.valueOf(26),
                lumpSum = BigDecimal.valueOf(27),
                unitCost = BigDecimal.valueOf(28),
                spfCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(29),
            ),
            deduction = BudgetCostsCalculationResultFull(
                staff = BigDecimal.valueOf(30),
                office = BigDecimal.valueOf(31),
                travel = BigDecimal.valueOf(32),
                external = BigDecimal.valueOf(33),
                equipment = BigDecimal.valueOf(34),
                infrastructure = BigDecimal.valueOf(35),
                other = BigDecimal.valueOf(36),
                lumpSum = BigDecimal.valueOf(37),
                unitCost = BigDecimal.valueOf(38),
                spfCost = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(39),
            ),
        )
    }

    @MockK
    private lateinit var certificateCostCategoryRepository: ReportProjectCertificateCostCategoryRepository
    @MockK
    private lateinit var spendingProfileRepository: ProjectReportSpendingProfileRepository
    @MockK
    private lateinit var expenditureCostCategoryRepository: ReportProjectPartnerExpenditureCostCategoryRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportCertificateCostCategoryPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(certificateCostCategoryRepository)
    }

    @Test
    fun getCostCategories() {
        every { certificateCostCategoryRepository
            .findFirstByReportEntityProjectIdAndReportEntityId(PROJECT_ID, reportId = 4L)
        } returns expenditureEntity()
        assertThat(persistence.getCostCategories(PROJECT_ID, reportId = 4L)).isEqualTo(expenditure)
    }

    @Test
    fun getCostCategoriesCumulative() {
        every { certificateCostCategoryRepository.findCumulativeForReportIds(setOf(42L, 43L)) } returns
            expenditurePreviouslyReportedWithParked.previouslyReported
        every { certificateCostCategoryRepository.findCumulativeVerifiedForReportIds(setOf(45L)) } returns expendituresPreviouslyVerified

        assertThat(persistence.getCostCategoriesCumulative(setOf(42L, 43L), setOf(45L))).isEqualTo(expected)
    }

    @Test
    fun getCostCategoriesPerPartner() {
        every { spendingProfileRepository.findAllByIdProjectReportIdOrderByPartnerNumber(projectReportId = 6L) } returns
            listOf(partnerProfile())
        every { expenditureCostCategoryRepository.findPartnerOverviewForProjectReport(PROJECT_ID, projectReportId = 6L) } returns
            listOf(perPartnerSum())
        assertThat(persistence.getCostCategoriesPerPartner(PROJECT_ID, reportId = 6L)).containsExactly(expectedPerPartner)
    }

    @Test
    fun updateCurrentlyReportedValues() {
        val entity = expenditureEntity()
        every { certificateCostCategoryRepository
            .findFirstByReportEntityProjectIdAndReportEntityId(PROJECT_ID, reportId = 4L)
        } returns entity
        persistence.updateCurrentlyReportedValues(PROJECT_ID, reportId = 4L,
            CertificateCostCategoryCurrentlyReported(expenditureCurrentlyReportedWithReIncluded.currentlyReported)
        )

        assertThat(entity.staffCurrent).isEqualTo(BigDecimal.valueOf(100))
        assertThat(entity.officeCurrent).isEqualTo(BigDecimal.valueOf(101))
        assertThat(entity.travelCurrent).isEqualTo(BigDecimal.valueOf(102))
        assertThat(entity.externalCurrent).isEqualTo(BigDecimal.valueOf(103))
        assertThat(entity.equipmentCurrent).isEqualTo(BigDecimal.valueOf(104))
        assertThat(entity.infrastructureCurrent).isEqualTo(BigDecimal.valueOf(105))
        assertThat(entity.otherCurrent).isEqualTo(BigDecimal.valueOf(106))
        assertThat(entity.lumpSumCurrent).isEqualTo(BigDecimal.valueOf(107))
        assertThat(entity.unitCostCurrent).isEqualTo(BigDecimal.valueOf(108))
        assertThat(entity.sumCurrent).isEqualTo(BigDecimal.valueOf(109))
    }

}
