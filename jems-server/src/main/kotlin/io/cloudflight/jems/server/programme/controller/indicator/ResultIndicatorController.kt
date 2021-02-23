package io.cloudflight.jems.server.programme.controller.indicator

import io.cloudflight.jems.api.programme.ResultIndicatorApi
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorSummaryDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.create_result_indicator.CreateResultIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.get_result_indicator.GetResultIndicatorInteractor
import io.cloudflight.jems.server.programme.service.indicator.list_result_indicators.ListResultIndicatorsInteractor
import io.cloudflight.jems.server.programme.service.indicator.update_result_indicator.UpdateResultIndicatorInteractor
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class ResultIndicatorController(
    private val listResultIndicators: ListResultIndicatorsInteractor,
    private val getResultIndicator: GetResultIndicatorInteractor,
    private val createResultIndicator: CreateResultIndicatorInteractor,
    private val updateResultIndicator: UpdateResultIndicatorInteractor,
) : ResultIndicatorApi {

    override fun getResultIndicatorDetails(pageable: Pageable) =
        listResultIndicators.getResultIndicatorDetails(pageable).toResultIndicatorDetailDTOPage()

    override fun getResultIndicatorDetail(id: Long) =
        getResultIndicator.getResultIndicator(id).toResultIndicatorDetailDTO()

    override fun createResultIndicator(resultIndicatorCreateRequestDTO: ResultIndicatorCreateRequestDTO) =
        createResultIndicator.createResultIndicator(resultIndicatorCreateRequestDTO.toResultIndicator())
            .toResultIndicatorDetailDTO()

    override fun updateResultIndicator(resultIndicatorUpdateRequestDTO: ResultIndicatorUpdateRequestDTO) =
        updateResultIndicator.updateResultIndicator(resultIndicatorUpdateRequestDTO.toResultIndicator())
            .toResultIndicatorDetailDTO()

    override fun getResultIndicatorSummaries(): Set<ResultIndicatorSummaryDTO> =
        listResultIndicators.getResultIndicatorSummaries().toResultIndicatorSummaryDTOSet()

    override fun getResultIndicatorSummariesForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<ResultIndicatorSummaryDTO> =
        listResultIndicators.getResultIndicatorSummariesForSpecificObjective(programmeObjectivePolicy)
            .toResultIndicatorSummaryDTOList()
}
