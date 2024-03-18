package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.unitCost

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslId
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportUnitCostRepository
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrentWithReIncluded
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectPartnerReportUnitCostPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 369L

        private val report = mockk<ProjectPartnerReportEntity>().also {
            every { it.partnerId } returns PARTNER_ID
        }

        private val unitCost = mockk<ProgrammeUnitCostEntity>().also {
            every { it.id } returns 337L
            every { it.translatedValues } returns mutableSetOf(
                ProgrammeUnitCostTranslEntity(
                    ProgrammeUnitCostTranslId(337L, SystemLanguage.EN),
                    name = "unit cost name EN",
                    description = "unit cost desc EN",
                ),
            )
        }

        private fun unitCostEntity(id: Long) = PartnerReportUnitCostEntity(
            id = id,
            reportEntity = report,
            programmeUnitCost = unitCost,
            numberOfUnits = BigDecimal.ONE,

            total = BigDecimal.valueOf(10),
            current = BigDecimal.valueOf(20),
            totalEligibleAfterControl = BigDecimal.valueOf(40),
            previouslyReported = BigDecimal.valueOf(30),
            currentParked = BigDecimal.ZERO,
            currentParkedVerification = BigDecimal.ZERO,
            currentReIncluded = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.valueOf(50),
            previouslyValidated = BigDecimal.valueOf(5)
        )

        private fun expectedUnitCost(id: Long) = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = id,
            unitCostId = 337L,
            name = setOf(InputTranslation(SystemLanguage.EN, "unit cost name EN")),
            totalEligibleBudget = BigDecimal.valueOf(10),
            previouslyReported = BigDecimal.valueOf(30),
            currentReport = BigDecimal.valueOf(20),
            totalEligibleAfterControl = BigDecimal.valueOf(40),
            totalReportedSoFar = BigDecimal.ZERO,
            totalReportedSoFarPercentage = BigDecimal.ZERO,
            remainingBudget = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.valueOf(50),
            previouslyValidated = BigDecimal.valueOf(5)
        )
    }

    @MockK
    lateinit var repository: ProjectPartnerReportUnitCostRepository

    @InjectMockKs
    lateinit var persistence: ProjectPartnerReportUnitCostPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(repository)
    }

    @Test
    fun getUnitCost() {
        every { repository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(partnerId = PARTNER_ID, reportId = 928L)
        } returns mutableListOf(unitCostEntity(14L))
        assertThat(persistence.getUnitCost(partnerId = PARTNER_ID, reportId = 928L)).containsExactly(expectedUnitCost(14L))
    }

    @Test
    fun getUnitCostCumulative() {
        every { repository.findCumulativeForReportIds(setOf(22L)) } returns listOf(Triple(337L, BigDecimal.TEN, BigDecimal.valueOf(50)))
        val result = persistence.getUnitCostCumulative(setOf(22L))
        assertThat(result.keys).containsExactly(337L)
        assertThat(result[337L]).isEqualTo(ExpenditureUnitCostCurrent(BigDecimal.TEN, BigDecimal.valueOf(50)))
    }

    @Test
    fun updateCurrentlyReportedValues() {
        val unitCost_79 = unitCostEntity(79L)
        val unitCost_80 = unitCostEntity(80L)
        val unitCost_81 = unitCostEntity(81L)
        every { repository.findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(PARTNER_ID, reportId = 921L) } returns
            mutableListOf(unitCost_79, unitCost_80, unitCost_81)
        persistence.updateCurrentlyReportedValues(
            PARTNER_ID,
            reportId = 921L,
            mapOf(
                80L to ExpenditureUnitCostCurrentWithReIncluded(
                    current = BigDecimal.valueOf(10),
                    currentReIncluded = BigDecimal.valueOf(100)
                )
            )
        )

        assertThat(unitCost_79.current).isEqualByComparingTo(BigDecimal.valueOf(20))
        assertThat(unitCost_80.current).isEqualByComparingTo(BigDecimal.TEN)
        assertThat(unitCost_81.current).isEqualByComparingTo(BigDecimal.valueOf(20))
    }

    @Test
    fun updateAfterControlValues() {
        val unitCost_79 = unitCostEntity(79L)
        val unitCost_80 = unitCostEntity(80L)
        val unitCost_81 = unitCostEntity(81L)
        every { repository.findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(PARTNER_ID, reportId = 922L) } returns
            mutableListOf(unitCost_79, unitCost_80, unitCost_81)
        persistence.updateAfterControlValues(
            PARTNER_ID,
            reportId = 922L,
            mapOf(80L to ExpenditureUnitCostCurrent(current = BigDecimal.TEN, currentParked = BigDecimal.valueOf(100)))
        )

        assertThat(unitCost_79.totalEligibleAfterControl).isEqualByComparingTo(BigDecimal.valueOf(40))
        assertThat(unitCost_80.totalEligibleAfterControl).isEqualByComparingTo(BigDecimal.TEN)
        assertThat(unitCost_81.totalEligibleAfterControl).isEqualByComparingTo(BigDecimal.valueOf(40))
    }

}
