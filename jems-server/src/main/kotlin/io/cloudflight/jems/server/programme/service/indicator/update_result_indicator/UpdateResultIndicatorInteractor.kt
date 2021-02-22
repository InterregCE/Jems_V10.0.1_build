package io.cloudflight.jems.server.programme.service.indicator.update_result_indicator

import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail

interface UpdateResultIndicatorInteractor {
    fun updateResultIndicator(resultIndicator: ResultIndicator): ResultIndicatorDetail
}
