package io.cloudflight.jems.server.programme.service.indicator.create_result_indicator

import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail

interface CreateResultIndicatorInteractor {
    fun createResultIndicator(resultIndicator: ResultIndicator): ResultIndicatorDetail
}
