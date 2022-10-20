package io.cloudflight.jems.server.project.repository.report.financialOverview.unitCost

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostTranslId
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportUnitCostEntity
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportUnitCostRepository
import io.cloudflight.jems.server.project.service.report.model.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportUnitCostPersistenceProviderTest : UnitTest() {

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
            previouslyReported = BigDecimal.valueOf(30),
        )

        private val newValues = mapOf(80L to BigDecimal.TEN)

        private fun expectedUnitCost(id: Long) = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = id,
            unitCostId = 337L,
            name = setOf(InputTranslation(SystemLanguage.EN, "unit cost name EN")),
            totalEligibleBudget = BigDecimal.valueOf(10),
            previouslyReported = BigDecimal.valueOf(30),
            currentReport = BigDecimal.valueOf(20),
            totalReportedSoFar = BigDecimal.ZERO,
            totalReportedSoFarPercentage = BigDecimal.ZERO,
            remainingBudget = BigDecimal.ZERO,
        )
    }

    @MockK
    lateinit var repository: ProjectPartnerReportUnitCostRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportUnitCostPersistenceProvider

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
        every { repository.findCumulativeForReportIds(setOf(22L)) } returns listOf(Pair(337L, BigDecimal.TEN))
        val result = persistence.getUnitCostCumulative(setOf(22L))
        assertThat(result.keys).containsExactly(337L)
        assertThat(result[337L]).isEqualByComparingTo(BigDecimal.TEN)
    }

    @Test
    fun updateCurrentlyReportedValues() {
        val unitCost_79 = unitCostEntity(79L)
        val unitCost_80 = unitCostEntity(80L)
        val unitCost_81 = unitCostEntity(81L)
        every { repository.findByReportEntityPartnerIdAndReportEntityIdOrderByIdAsc(PARTNER_ID, reportId = 921L) } returns
            mutableListOf(unitCost_79, unitCost_80, unitCost_81)
        persistence.updateCurrentlyReportedValues(PARTNER_ID, reportId = 921L, newValues)

        assertThat(unitCost_79.current).isEqualByComparingTo(BigDecimal.valueOf(20))
        assertThat(unitCost_80.current).isEqualByComparingTo(BigDecimal.TEN)
        assertThat(unitCost_81.current).isEqualByComparingTo(BigDecimal.valueOf(20))
    }

}
