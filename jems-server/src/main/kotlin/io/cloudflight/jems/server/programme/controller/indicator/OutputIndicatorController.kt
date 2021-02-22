package io.cloudflight.jems.server.programme.controller.indicator

import io.cloudflight.jems.api.programme.OutputIndicatorApi
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorSummaryDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.create_output_indicator.CreateOutputIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.get_output_indicator.GetOutputIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.list_output_indicators.ListOutputIndicatorsInteractor
import io.cloudflight.jems.server.programme.service.indicator.update_output_indicator.UpdateOutputIndicatorInteractor
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class OutputIndicatorController(
    private val listOutputIndicators: ListOutputIndicatorsInteractor,
    private val getOutputIndicator: GetOutputIndicatorInteractor,
    private val createOutputIndicator: CreateOutputIndicatorInteractor,
    private val updateOutputIndicator: UpdateOutputIndicatorInteractor,
) : OutputIndicatorApi {

    override fun getOutputIndicatorDetails(pageable: Pageable) =
        listOutputIndicators.getOutputIndicatorDetails(pageable).toOutputIndicatorDetailDTOPage()

    override fun getOutputIndicatorSummaries() =
        listOutputIndicators.getOutputIndicatorSummaries().toOutputIndicatorSummaryDTOSet()

    override fun getOutputIndicatorSummariesForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<OutputIndicatorSummaryDTO> =
        listOutputIndicators.getOutputIndicatorSummariesForSpecificObjective(programmeObjectivePolicy)
            .toOutputIndicatorSummaryDTOList()

    override fun getOutputIndicatorDetail(id: Long) =
        getOutputIndicator.getOutputIndicator(id).toOutputIndicatorDetailDTO()

    override fun createOutputIndicator(outputIndicatorCreateRequestDTO: OutputIndicatorCreateRequestDTO) =
        createOutputIndicator.createOutputIndicator(outputIndicatorCreateRequestDTO.toOutputIndicator())
            .toOutputIndicatorDetailDTO()

    override fun updateOutputIndicator(outputIndicatorUpdateRequestDTO: OutputIndicatorUpdateRequestDTO) =
        updateOutputIndicator.updateOutputIndicator(outputIndicatorUpdateRequestDTO.toOutputIndicator())
            .toOutputIndicatorDetailDTO()
}
