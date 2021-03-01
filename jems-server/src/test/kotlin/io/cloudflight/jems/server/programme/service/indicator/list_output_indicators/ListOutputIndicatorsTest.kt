package io.cloudflight.jems.server.programme.service.indicator.list_output_indicators

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable


internal class ListOutputIndicatorsTest : IndicatorsBaseTest() {

    private val outputIndicatorSummary = OutputIndicatorSummary(
        1L,
        "ID01",
        "ioCODE",
        indicatorNameSet,
        indicatorProgrammeSpecificObjectiveCode,
        indicatorMeasurementUnitSet,
    )

    @MockK
    lateinit var persistence: OutputIndicatorPersistence

    @InjectMockKs
    lateinit var listOutputIndicators: ListOutputIndicators

    @Test
    fun `should return a page of output indicator details`() {
        val outputIndicatorDetail = buildOutputIndicatorDetailInstance()
        every { persistence.getOutputIndicators(Pageable.unpaged()) } returns PageImpl(listOf(outputIndicatorDetail))
        assertThat(listOutputIndicators.getOutputIndicatorDetails(Pageable.unpaged()))
            .isEqualTo(PageImpl(listOf(outputIndicatorDetail)))
    }

    @Test
    fun `should return a set of output indicator summaries`() {
        every { persistence.getTop50OutputIndicators() } returns setOf(outputIndicatorSummary)
        assertThat(listOutputIndicators.getOutputIndicatorSummaries())
            .isEqualTo(setOf(outputIndicatorSummary))
    }

    @Test
    fun `should return a list of output indicator summaries for a specific objective`() {
        every { persistence.getOutputIndicatorsForSpecificObjective(ProgrammeObjectivePolicy.RenewableEnergy) } returns listOf(
            outputIndicatorSummary
        )
        assertThat(listOutputIndicators.getOutputIndicatorSummariesForSpecificObjective(ProgrammeObjectivePolicy.RenewableEnergy))
            .isEqualTo(listOf(outputIndicatorSummary))
    }

}
