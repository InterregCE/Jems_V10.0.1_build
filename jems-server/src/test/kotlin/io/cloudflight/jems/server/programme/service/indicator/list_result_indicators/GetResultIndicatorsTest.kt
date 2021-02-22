package io.cloudflight.jems.server.programme.service.indicator.list_result_indicators

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class GetResultIndicatorsTest : IndicatorsBaseTest() {

    private val resultIndicatorSummary = ResultIndicatorSummary(
        1L,
        "ID01",
        "ioCODE",
        "indicator title",
        indicatorProgrammeSpecificObjectiveCode,
        "measurement unit",
    )

    @MockK
    lateinit var persistence: ResultIndicatorPersistence

    @InjectMockKs
    lateinit var getResultIndicators: ListResultIndicators

    @Test
    fun `should return a page of result indicator details`() {
        val resultIndicatorDetail = buildResultIndicatorDetailInstance()
        every { persistence.getResultIndicators(Pageable.unpaged()) } returns PageImpl(listOf(resultIndicatorDetail))
        assertThat(getResultIndicators.getResultIndicatorDetails(Pageable.unpaged()))
            .isEqualTo(PageImpl(listOf(resultIndicatorDetail)))
    }

    @Test
    fun `should return a set of result indicator summaries`() {
        every { persistence.getTop50ResultIndicators() } returns setOf(resultIndicatorSummary)
        assertThat(getResultIndicators.getResultIndicatorSummaries())
            .isEqualTo(setOf(resultIndicatorSummary))
    }

    @Test
    fun `should return a list of result indicator summaries for a specific objective`() {
        every { persistence.getResultIndicatorsForSpecificObjective(ProgrammeObjectivePolicy.RenewableEnergy) } returns listOf(
            resultIndicatorSummary
        )
        assertThat(
            getResultIndicators.getResultIndicatorSummariesForSpecificObjective(ProgrammeObjectivePolicy.RenewableEnergy)
        ).isEqualTo(listOf(resultIndicatorSummary))
    }
}
