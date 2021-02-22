package io.cloudflight.jems.server.programme.controller.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.create_output_indicator.CreateOutputIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.get_output_indicator.GetOutputIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.list_output_indicators.ListOutputIndicatorsInteractor
import io.cloudflight.jems.server.programme.service.indicator.update_output_indicator.UpdateOutputIndicatorInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class OutputIndicatorControllerTest : IndicatorsControllerBaseTest() {

    @MockK
    private lateinit var listOutputIndicators: ListOutputIndicatorsInteractor

    @MockK
    private lateinit var getOutputIndicator: GetOutputIndicatorInteractor

    @MockK
    private lateinit var createOutputIndicator: CreateOutputIndicatorInteractor

    @MockK
    private lateinit var updateOutputIndicator: UpdateOutputIndicatorInteractor

    @InjectMockKs
    private lateinit var outputIndicatorController: OutputIndicatorController

    @Test
    fun `should return output indicator detail`() {
        val outputIndicatorDetail = buildOutputIndicatorDetailInstance()
        every { getOutputIndicator.getOutputIndicator(indicatorId) } returns outputIndicatorDetail
        assertThat(
            outputIndicatorController.getOutputIndicatorDetail(indicatorId)
        ).isEqualTo(outputIndicatorDetail.toOutputIndicatorDetailDTO())
    }

    @Test
    fun `should return set of output indicator summaries`() {
        val outputIndicatorSummary = buildOutputIndicatorSummaryInstance()
        every { listOutputIndicators.getOutputIndicatorSummaries() } returns setOf(outputIndicatorSummary)
        assertThat(
            outputIndicatorController.getOutputIndicatorSummaries()
        ).isEqualTo(setOf(outputIndicatorSummary).toOutputIndicatorSummaryDTOSet())
    }

    @Test
    fun `should return a page of output indicator details`() {
        val outputIndicatorDetail = buildOutputIndicatorDetailInstance()
        every { listOutputIndicators.getOutputIndicatorDetails(Pageable.unpaged()) } returns PageImpl(
            listOf(outputIndicatorDetail)
        )
        assertThat(
            outputIndicatorController.getOutputIndicatorDetails(Pageable.unpaged())
        ).isEqualTo(PageImpl(listOf(outputIndicatorDetail)).toOutputIndicatorDetailDTOPage())
    }

    @Test
    fun `should return a list of output indicator summary for a specific objective`() {
        val outputIndicatorSummary = buildOutputIndicatorSummaryInstance()
        every {
            listOutputIndicators.getOutputIndicatorSummariesForSpecificObjective(
                ProgrammeObjectivePolicy.RenewableEnergy
            )
        } returns listOf(outputIndicatorSummary)
        assertThat(
            outputIndicatorController.getOutputIndicatorSummariesForSpecificObjective(ProgrammeObjectivePolicy.RenewableEnergy)
        ).isEqualTo(listOf(outputIndicatorSummary).toOutputIndicatorSummaryDTOList())
    }

    @Test
    fun `should create and return the output indicator detail`() {
        val outputIndicatorDetail = buildOutputIndicatorDetailInstance()
        every {
            createOutputIndicator.createOutputIndicator(
                outputIndicatorCreateRequestDTO.toOutputIndicator()
            )
        } returns outputIndicatorDetail
        assertThat(
            outputIndicatorController.createOutputIndicator(outputIndicatorCreateRequestDTO)
        ).isEqualTo(outputIndicatorDetail.toOutputIndicatorDetailDTO())
    }

    @Test
    fun `should update and return the output indicator detail`() {
        val outputIndicatorDetail = buildOutputIndicatorDetailInstance()
        every {
            updateOutputIndicator.updateOutputIndicator(
                outputIndicatorUpdateRequestDTO.toOutputIndicator()
            )
        } returns outputIndicatorDetail
        assertThat(
            outputIndicatorController.updateOutputIndicator(outputIndicatorUpdateRequestDTO)
        ).isEqualTo(outputIndicatorDetail.toOutputIndicatorDetailDTO())
    }

}
