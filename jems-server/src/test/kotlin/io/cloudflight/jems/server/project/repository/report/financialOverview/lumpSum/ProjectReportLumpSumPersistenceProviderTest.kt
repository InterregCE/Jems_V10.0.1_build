package io.cloudflight.jems.server.project.repository.report.financialOverview.lumpSum

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumTranslEntity
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeLumpSumTranslId
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportLumpSumEntity
import io.cloudflight.jems.server.project.repository.report.expenditure.ProjectPartnerReportLumpSumRepository
import io.cloudflight.jems.server.project.service.report.model.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportLumpSumPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 362L

        private val report = mockk<ProjectPartnerReportEntity>().also {
            every { it.partnerId } returns 4L
        }

        private val lumpSum = mockk<ProgrammeLumpSumEntity>().also {
            every { it.id } returns 335L
            every { it.translatedValues } returns mutableSetOf(
                ProgrammeLumpSumTranslEntity(
                    ProgrammeLumpSumTranslId(335L, SystemLanguage.EN),
                    name = "lump sum name EN",
                    description = "lump sum desc EN",
                )
            )
        }

        private fun lumpSumEntity(id: Long) = PartnerReportLumpSumEntity(
            id = id,
            reportEntity = report,
            programmeLumpSum = lumpSum,
            orderNr = 9,
            period = 255,

            total = BigDecimal.valueOf(10),
            current = BigDecimal.valueOf(20),
            previouslyReported = BigDecimal.valueOf(30),
            previouslyPaid = BigDecimal.valueOf(40),
        )

        private val newValues = mapOf(90L to BigDecimal.TEN)

        private fun expectedLumpSum(id: Long) = ExpenditureLumpSumBreakdownLine(
            reportLumpSumId = id,
            lumpSumId = 335L,
            period = 255,
            name = setOf(InputTranslation(SystemLanguage.EN, "lump sum name EN")),
            totalEligibleBudget = BigDecimal.valueOf(10),
            previouslyReported = BigDecimal.valueOf(30),
            previouslyPaid = BigDecimal.valueOf(40),
            currentReport = BigDecimal.valueOf(20),
            totalReportedSoFar = BigDecimal.ZERO,
            totalReportedSoFarPercentage = BigDecimal.ZERO,
            remainingBudget = BigDecimal.ZERO,
        )
    }

    @MockK
    lateinit var repository: ProjectPartnerReportLumpSumRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportLumpSumPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(repository)
    }

    @Test
    fun getLumpSum() {
        every { repository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(partnerId = 15L, reportId = 988L)
        } returns mutableListOf(lumpSumEntity(14L))
        assertThat(persistence.getLumpSum(partnerId = 15L, reportId = 988L)).containsExactly(expectedLumpSum(14L))
    }

    @Test
    fun getLumpSumCumulative() {
        every { repository.findCumulativeForReportIds(setOf(22L)) } returns listOf(Pair(4, BigDecimal.TEN))
        val result = persistence.getLumpSumCumulative(setOf(22L))
        assertThat(result.keys).containsExactly(4)
        assertThat(result.get(4)).isEqualByComparingTo(BigDecimal.TEN)
    }

    @Test
    fun updateCurrentlyReportedValues() {
        val lumpSum_89 = lumpSumEntity(89L)
        val lumpSum_90 = lumpSumEntity(90L)
        val lumpSum_91 = lumpSumEntity(91L)
        every { repository.findByReportEntityPartnerIdAndReportEntityIdOrderByOrderNrAscIdAsc(PARTNER_ID, reportId = 4L) } returns
            mutableListOf(lumpSum_89, lumpSum_90, lumpSum_91)
        persistence.updateCurrentlyReportedValues(PARTNER_ID, reportId = 4L, newValues)

        assertThat(lumpSum_89.current).isEqualByComparingTo(BigDecimal.valueOf(20))
        assertThat(lumpSum_90.current).isEqualByComparingTo(BigDecimal.TEN)
        assertThat(lumpSum_91.current).isEqualByComparingTo(BigDecimal.valueOf(20))
    }

}
