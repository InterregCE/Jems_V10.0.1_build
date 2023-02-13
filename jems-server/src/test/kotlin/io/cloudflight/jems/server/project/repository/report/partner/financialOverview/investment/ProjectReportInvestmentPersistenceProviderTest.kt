package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.investment

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.partner.expenditure.PartnerReportInvestmentTranslEntity
import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportInvestmentRepository
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrentWithReIncluded
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportInvestmentPersistenceProviderTest : UnitTest() {

    companion object {
        private fun investment(id: Long) = PartnerReportInvestmentEntity(
            id = id,
            reportEntity = mockk(),
            investmentId = 245L,
            investmentNumber = 2,
            workPackageNumber = 8,
            translatedValues = mutableSetOf(
                PartnerReportInvestmentTranslEntity(TranslationId(mockk(), SystemLanguage.EN), "title EN investment")
            ),
            total = BigDecimal.TEN,
            current = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ONE,
            previouslyReportedParked = BigDecimal.valueOf(1000),
            currentReIncluded = BigDecimal.valueOf(101),
            currentParked = BigDecimal.valueOf(100)
        )

        private val expectedInvestment = ExpenditureInvestmentBreakdownLine(
            reportInvestmentId = 20L,
            investmentId = 245L,
            investmentNumber = 2,
            workPackageNumber = 8,
            title = setOf(InputTranslation(SystemLanguage.EN, "title EN investment")),
            totalEligibleBudget = BigDecimal.TEN,
            previouslyReported = BigDecimal.ONE,
            currentReport = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.ZERO,
            previouslyReportedParked = BigDecimal.valueOf(1000),
            currentReportReIncluded = BigDecimal.valueOf(101),
        )

    }

    @MockK
    lateinit var repository: ProjectPartnerReportInvestmentRepository

    @InjectMockKs
    lateinit var persistence: ProjectPartnerReportInvestmentPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(repository)
    }

    @Test
    fun getInvestments() {
        val partnerId = 452L
        val reportId = 696L

        every { repository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(partnerId, reportId = reportId)
        } returns mutableListOf(investment(20L))
        assertThat(persistence.getInvestments(partnerId, reportId = reportId)).containsExactly(expectedInvestment)
    }

    @Test
    fun getInvestmentsCumulative() {
        val reportId = 672L

        every { repository.findCumulativeForReportIds(reportIds = setOf(reportId)) } returns
            listOf(Triple(5L, BigDecimal.ONE, BigDecimal.TEN))
        assertThat(persistence.getInvestmentsCumulative(reportIds = setOf(reportId)))
            .containsExactlyEntriesOf(mapOf(5L to ExpenditureInvestmentCurrent( current = BigDecimal.ONE, currentParked = BigDecimal.TEN)))
    }

    @Test
    fun updateCurrentlyReportedValues() {
        val partnerId = 458L
        val reportId = 697L

        val investment_15 = investment(15L)
        val investment_16 = investment(16L)
        val investment_17 = investment(17L)

        every { repository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(partnerId, reportId = reportId)
        } returns mutableListOf(investment_15, investment_16, investment_17)
        persistence.updateCurrentlyReportedValues(
            partnerId,
            reportId = reportId,
            mapOf(
                16L to ExpenditureInvestmentCurrentWithReIncluded(
                    current = BigDecimal.TEN,
                    currentReIncluded = BigDecimal.ZERO
                )
            )
        )

        assertThat(investment_15.current).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(investment_15.currentParked).isEqualByComparingTo(BigDecimal.valueOf(100))
        assertThat(investment_16.current).isEqualByComparingTo(BigDecimal.TEN)
        assertThat(investment_17.current).isEqualByComparingTo(BigDecimal.ZERO)
    }

    @Test
    fun updateAfterControlValues() {
        val partnerId = 457L
        val reportId = 697L

        val investment_15 = investment(15L)
        val investment_16 = investment(16L)
        val investment_17 = investment(17L)

        every { repository
            .findByReportEntityPartnerIdAndReportEntityIdOrderByWorkPackageNumberAscInvestmentNumberAsc(partnerId, reportId = reportId)
        } returns mutableListOf(investment_15, investment_16, investment_17)
        persistence.updateAfterControlValues(
            partnerId,
            reportId = reportId,
            mapOf(
                17L to ExpenditureInvestmentCurrent(
                    current = BigDecimal.TEN,
                    currentParked = BigDecimal.valueOf(100)
                )
            )
        )

        assertThat(investment_15.totalEligibleAfterControl).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(investment_16.totalEligibleAfterControl).isEqualByComparingTo(BigDecimal.ZERO)
        assertThat(investment_17.totalEligibleAfterControl).isEqualByComparingTo(BigDecimal.TEN)
    }

}
