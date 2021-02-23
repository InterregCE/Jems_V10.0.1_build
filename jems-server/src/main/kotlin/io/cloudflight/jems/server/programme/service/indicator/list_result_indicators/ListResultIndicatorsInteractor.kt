package io.cloudflight.jems.server.programme.service.indicator.list_result_indicators

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListResultIndicatorsInteractor {
    fun getResultIndicatorDetails(pageable: Pageable): Page<ResultIndicatorDetail>
    fun getResultIndicatorSummaries(): Set<ResultIndicatorSummary>
    fun getResultIndicatorSummariesForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<ResultIndicatorSummary>
}
