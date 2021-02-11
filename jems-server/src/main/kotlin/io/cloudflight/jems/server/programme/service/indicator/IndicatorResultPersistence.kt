package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorResultDto

interface IndicatorResultPersistence {
    fun getResultIndicatorsDetails(): Set<IndicatorResultDto>
    fun getResultIndicatorsForSpecificObjective(code: String): List<IndicatorResultDto>
}
