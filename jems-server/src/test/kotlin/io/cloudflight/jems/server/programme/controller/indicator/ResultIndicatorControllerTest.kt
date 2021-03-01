package io.cloudflight.jems.server.programme.controller.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.create_result_indicator.CreateResultIndicatorException
import io.cloudflight.jems.server.programme.service.indicator.create_result_indicator.CreateResultIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.get_result_indicator.GetResultIndicatorException
import io.cloudflight.jems.server.programme.service.indicator.get_result_indicator.GetResultIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.list_result_indicators.GetResultIndicatorDetailsException
import io.cloudflight.jems.server.programme.service.indicator.list_result_indicators.GetResultIndicatorSummariesException
import io.cloudflight.jems.server.programme.service.indicator.list_result_indicators.GetResultIndicatorSummariesForSpecificObjectiveException
import io.cloudflight.jems.server.programme.service.indicator.list_result_indicators.ListResultIndicatorsInteractor
import io.cloudflight.jems.server.programme.service.indicator.update_result_indicator.UpdateResultIndicatorException
import io.cloudflight.jems.server.programme.service.indicator.update_result_indicator.UpdateResultIndicatorInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.lang.Exception

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
    fun `should throw GetResultIndicatorException when there is a problem in getting result indicator detail`() {
        val exception = GetResultIndicatorException(Exception())
        every { getResultIndicator.getResultIndicator(indicatorId) } throws exception
        Assertions.assertThatExceptionOfType(GetResultIndicatorException::class.java).isThrownBy {
            resultIndicatorController.getResultIndicatorDetail(indicatorId)
        }
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
    fun `should throw GetResultIndicatorSummariesException when there is a problem in getting set of result indicator summaries`() {
        val exception = GetResultIndicatorSummariesException(Exception())
        every { listResultIndicators.getResultIndicatorSummaries() } throws exception
        Assertions.assertThatExceptionOfType(GetResultIndicatorSummariesException::class.java).isThrownBy {
            resultIndicatorController.getResultIndicatorSummaries()
        }
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
    fun `should throw GetResultIndicatorDetailsException when there is a problem in getting a page of result indicator details`() {
        val exception = GetResultIndicatorDetailsException(Exception())
        every { listResultIndicators.getResultIndicatorDetails(Pageable.unpaged()) } throws exception
        Assertions.assertThatExceptionOfType(GetResultIndicatorDetailsException::class.java).isThrownBy {
            resultIndicatorController.getResultIndicatorDetails(Pageable.unpaged())
        }
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
    fun `should throw GetResultIndicatorSummariesForSpecificObjectiveException when there is a problem in getting list of result indicator summaries for a specific objective`() {
        val exception = GetResultIndicatorSummariesForSpecificObjectiveException(Exception())
        every { listResultIndicators.getResultIndicatorSummariesForSpecificObjective(ProgrammeObjectivePolicy.Digitalization) } throws exception
        Assertions.assertThatExceptionOfType(GetResultIndicatorSummariesForSpecificObjectiveException::class.java).isThrownBy {
            resultIndicatorController.getResultIndicatorSummariesForSpecificObjective(ProgrammeObjectivePolicy.Digitalization)
        }
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
    fun `should throw CreateResultIndicatorException when there is a problem in creating an result indicator`() {
        val exception = CreateResultIndicatorException(Exception())
        every { createResultIndicator.createResultIndicator(resultIndicatorCreateRequestDTO.toResultIndicator()) } throws exception
        Assertions.assertThatExceptionOfType(CreateResultIndicatorException::class.java).isThrownBy {
            resultIndicatorController.createResultIndicator(resultIndicatorCreateRequestDTO)
        }
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

    @Test
    fun `should throw UpdateResultIndicatorException when there is a problem in updating an result indicator`() {
        val exception = UpdateResultIndicatorException(Exception())
        every { updateResultIndicator.updateResultIndicator(resultIndicatorUpdateRequestDTO.toResultIndicator()) } throws exception
        Assertions.assertThatExceptionOfType(UpdateResultIndicatorException::class.java).isThrownBy {
            resultIndicatorController.updateResultIndicator(resultIndicatorUpdateRequestDTO)
        }
    }
}
