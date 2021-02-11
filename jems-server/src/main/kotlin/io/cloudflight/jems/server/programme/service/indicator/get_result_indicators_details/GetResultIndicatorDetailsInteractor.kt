package io.cloudflight.jems.server.programme.service.indicator.get_result_indicators_details

import io.cloudflight.jems.api.programme.dto.indicator.IndicatorResultDto

interface GetResultIndicatorDetailsInteractor {
    fun getResultIndicatorsDetails(): Set<IndicatorResultDto>
    fun getResultIndicatorsDetailsForSpecificObjective(code: String): List<IndicatorResultDto>
}
