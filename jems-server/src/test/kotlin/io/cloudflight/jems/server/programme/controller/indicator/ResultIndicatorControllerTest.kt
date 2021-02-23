package io.cloudflight.jems.server.programme.controller.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.create_result_indicator.CreateResultIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.get_result_indicator.GetResultIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.list_result_indicators.ListResultIndicatorsInteractor
import io.cloudflight.jems.server.programme.service.indicator.update_result_indicator.UpdateResultIndicatorInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

internal class ResultIndicatorControllerTest : IndicatorsControllerBaseTest() {

    @MockK
    private lateinit var listResultIndicators: ListResultIndicatorsInteractor

    @MockK
    private lateinit var getResultIndicator: GetResultIndicatorInteractor

    @MockK
    private lateinit var createResultIndicator: CreateResultIndicatorInteractor

    @MockK
    private lateinit var updateResultIndicator: UpdateResultIndicatorInteractor

    @InjectMockKs
    private lateinit var resultIndicatorController: ResultIndicatorController

    @Test
    fun `should return result indicator detail`() {
        val resultIndicatorDetail = buildResultIndicatorDetailInstance()
        every { getResultIndicator.getResultIndicator(indicatorId) } returns resultIndicatorDetail
        Assertions.assertThat(
            resultIndicatorController.getResultIndicatorDetail(indicatorId)
        ).isEqualTo(resultIndicatorDetail.toResultIndicatorDetailDTO())
    }

    @Test
    fun `should return set of result indicator summaries`() {
        val resultIndicatorSummary = buildResultIndicatorSummaryInstance()
        every { listResultIndicators.getResultIndicatorSummaries() } returns setOf(resultIndicatorSummary)
        Assertions.assertThat(
            resultIndicatorController.getResultIndicatorSummaries()
        ).isEqualTo(setOf(resultIndicatorSummary).toResultIndicatorSummaryDTOSet())
    }

    @Test
    fun `should return a page of result indicator details`() {
        val resultIndicatorDetail = buildResultIndicatorDetailInstance()
        every { listResultIndicators.getResultIndicatorDetails(Pageable.unpaged()) } returns PageImpl(
            listOf(resultIndicatorDetail)
        )
        Assertions.assertThat(
            resultIndicatorController.getResultIndicatorDetails(Pageable.unpaged())
        ).isEqualTo(PageImpl(listOf(resultIndicatorDetail)).toResultIndicatorDetailDTOPage())
    }

    @Test
    fun `should return a list of result indicator summary for a specific objective`() {
        val resultIndicatorSummary = buildResultIndicatorSummaryInstance()
        every {
            listResultIndicators.getResultIndicatorSummariesForSpecificObjective(
                ProgrammeObjectivePolicy.RenewableEnergy
            )
        } returns listOf(resultIndicatorSummary)
        Assertions.assertThat(
            resultIndicatorController.getResultIndicatorSummariesForSpecificObjective(ProgrammeObjectivePolicy.RenewableEnergy)
        ).isEqualTo(listOf(resultIndicatorSummary).toResultIndicatorSummaryDTOList())
    }

    @Test
    fun `should create and return the result indicator detail`() {
        val resultIndicatorDetail = buildResultIndicatorDetailInstance()
        every {
            createResultIndicator.createResultIndicator(
                resultIndicatorCreateRequestDTO.toResultIndicator()
            )
        } returns resultIndicatorDetail
        Assertions.assertThat(
            resultIndicatorController.createResultIndicator(resultIndicatorCreateRequestDTO)
        ).isEqualTo(resultIndicatorDetail.toResultIndicatorDetailDTO())
    }

    @Test
    fun `should update and return the result indicator detail`() {
        val resultIndicatorDetail = buildResultIndicatorDetailInstance()
        every {
            updateResultIndicator.updateResultIndicator(
                resultIndicatorUpdateRequestDTO.toResultIndicator()
            )
        } returns resultIndicatorDetail
        Assertions.assertThat(
            resultIndicatorController.updateResultIndicator(resultIndicatorUpdateRequestDTO)
        ).isEqualTo(resultIndicatorDetail.toResultIndicatorDetailDTO())
    }
}
