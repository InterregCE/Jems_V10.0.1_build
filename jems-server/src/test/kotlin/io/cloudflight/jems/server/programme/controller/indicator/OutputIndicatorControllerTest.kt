package io.cloudflight.jems.server.programme.controller.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.create_output_indicator.CreateOutputIndicatorException
import io.cloudflight.jems.server.programme.service.indicator.create_output_indicator.CreateOutputIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.get_output_indicator.GetOutputIndicatorException
import io.cloudflight.jems.server.programme.service.indicator.get_output_indicator.GetOutputIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.list_output_indicators.GetOutputIndicatorDetailsException
import io.cloudflight.jems.server.programme.service.indicator.list_output_indicators.GetOutputIndicatorSummariesException
import io.cloudflight.jems.server.programme.service.indicator.list_output_indicators.GetOutputIndicatorSummariesForSpecificObjectiveException
import io.cloudflight.jems.server.programme.service.indicator.list_output_indicators.ListOutputIndicatorsInteractor
import io.cloudflight.jems.server.programme.service.indicator.update_output_indicator.UpdateOutputIndicatorException
import io.cloudflight.jems.server.programme.service.indicator.update_output_indicator.UpdateOutputIndicatorInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.lang.Exception

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
    fun `should throw GetOutputIndicatorException when there is a problem in getting output indicator detail`() {
        val exception = GetOutputIndicatorException(Exception())
        every { getOutputIndicator.getOutputIndicator(indicatorId) } throws exception
        Assertions.assertThatExceptionOfType(GetOutputIndicatorException::class.java).isThrownBy {
            outputIndicatorController.getOutputIndicatorDetail(indicatorId)
        }
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
    fun `should throw GetOutputIndicatorSummariesException when there is a problem in getting set of output indicator summaries`() {
        val exception = GetOutputIndicatorSummariesException(Exception())
        every { listOutputIndicators.getOutputIndicatorSummaries() } throws exception
        Assertions.assertThatExceptionOfType(GetOutputIndicatorSummariesException::class.java).isThrownBy {
            outputIndicatorController.getOutputIndicatorSummaries()
        }
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
    fun `should throw GetOutputIndicatorDetailsException when there is a problem in getting a page of output indicator details`() {
        val exception = GetOutputIndicatorDetailsException(Exception())
        every { listOutputIndicators.getOutputIndicatorDetails(Pageable.unpaged()) } throws exception
        Assertions.assertThatExceptionOfType(GetOutputIndicatorDetailsException::class.java).isThrownBy {
            outputIndicatorController.getOutputIndicatorDetails(Pageable.unpaged())
        }
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
    fun `should throw GetOutputIndicatorSummariesForSpecificObjectiveException when there is a problem in getting list of output indicator summaries for a specific objective`() {
        val exception = GetOutputIndicatorSummariesForSpecificObjectiveException(Exception())
        every { listOutputIndicators.getOutputIndicatorSummariesForSpecificObjective(ProgrammeObjectivePolicy.Digitalization) } throws exception
        Assertions.assertThatExceptionOfType(GetOutputIndicatorSummariesForSpecificObjectiveException::class.java).isThrownBy {
            outputIndicatorController.getOutputIndicatorSummariesForSpecificObjective(ProgrammeObjectivePolicy.Digitalization)
        }
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
    fun `should throw CreateOutputIndicatorException when there is a problem in creating an output indicator`() {
        val exception = CreateOutputIndicatorException(Exception())
        every { createOutputIndicator.createOutputIndicator(outputIndicatorCreateRequestDTO.toOutputIndicator()) } throws exception
        Assertions.assertThatExceptionOfType(CreateOutputIndicatorException::class.java).isThrownBy {
            outputIndicatorController.createOutputIndicator(outputIndicatorCreateRequestDTO)
        }
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

    @Test
    fun `should throw UpdateOutputIndicatorException when there is a problem in updating an output indicator`() {
        val exception = UpdateOutputIndicatorException(Exception())
        every { updateOutputIndicator.updateOutputIndicator(outputIndicatorUpdateRequestDTO.toOutputIndicator()) } throws exception
        Assertions.assertThatExceptionOfType(UpdateOutputIndicatorException::class.java).isThrownBy {
            outputIndicatorController.updateOutputIndicator(outputIndicatorUpdateRequestDTO)
        }
    }

}
