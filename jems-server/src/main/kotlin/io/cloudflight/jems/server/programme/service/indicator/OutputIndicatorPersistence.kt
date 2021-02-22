package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OutputIndicatorPersistence {
    fun getCountOfOutputIndicators(): Long
    fun getOutputIndicator(id: Long): OutputIndicatorDetail
    fun getTop50OutputIndicators(): Set<OutputIndicatorSummary>
    fun getOutputIndicators(pageable: Pageable): Page<OutputIndicatorDetail>
    fun getOutputIndicatorsForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<OutputIndicatorSummary>
    fun saveOutputIndicator(outputIndicator: OutputIndicator): OutputIndicatorDetail
    fun isIdentifierUsedByAnotherOutputIndicator(outputIndicatorId: Long?, identifier: String): Boolean

}
