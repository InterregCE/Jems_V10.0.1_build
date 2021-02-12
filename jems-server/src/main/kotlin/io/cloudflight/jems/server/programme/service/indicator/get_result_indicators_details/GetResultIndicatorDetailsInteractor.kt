package io.cloudflight.jems.server.programme.service.indicator.get_result_indicators_details

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorResultDto
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy

interface GetResultIndicatorDetailsInteractor {
    fun getResultIndicatorsDetails(): Set<IndicatorResultDto>
    fun getResultIndicatorsDetailsForSpecificObjective(programmeObjectivePolicy: ProgrammeObjectivePolicy): List<IndicatorResultDto>
}
