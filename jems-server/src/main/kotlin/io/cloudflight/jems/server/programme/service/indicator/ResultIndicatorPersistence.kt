package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ResultIndicatorPersistence {
    fun getCountOfResultIndicators(): Long
    fun getResultIndicator(id: Long): ResultIndicatorDetail
    fun getTop50ResultIndicators(): Set<ResultIndicatorSummary>
    fun getResultIndicators(pageable: Pageable): Page<ResultIndicatorDetail>
    fun getResultIndicatorsForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<ResultIndicatorSummary>
    fun saveResultIndicator(resultIndicator: ResultIndicator): ResultIndicatorDetail
    fun isIdentifierUsedByAnotherResultIndicator(resultIndicatorId: Long?, identifier: String): Boolean
}
