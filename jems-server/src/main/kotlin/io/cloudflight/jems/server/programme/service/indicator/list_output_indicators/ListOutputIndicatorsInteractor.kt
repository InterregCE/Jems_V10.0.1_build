package io.cloudflight.jems.server.programme.service.indicator.list_output_indicators

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListOutputIndicatorsInteractor {
    fun getOutputIndicatorDetails(pageable: Pageable): Page<OutputIndicatorDetail>
    fun getOutputIndicatorSummaries(): Set<OutputIndicatorSummary>
    fun getOutputIndicatorSummariesForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<OutputIndicatorSummary>
}
