package io.cloudflight.jems.server.programme.service.indicator.get_result_indicator

import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail

interface GetResultIndicatorInteractor {
    fun getResultIndicator(id: Long): ResultIndicatorDetail
}
