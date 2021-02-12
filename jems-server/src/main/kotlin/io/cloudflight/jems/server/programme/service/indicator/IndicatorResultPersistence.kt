package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorResultDto
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy

interface IndicatorResultPersistence {
    fun getResultIndicatorsDetails(): Set<IndicatorResultDto>
    fun getResultIndicatorsForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<IndicatorResultDto>
}
