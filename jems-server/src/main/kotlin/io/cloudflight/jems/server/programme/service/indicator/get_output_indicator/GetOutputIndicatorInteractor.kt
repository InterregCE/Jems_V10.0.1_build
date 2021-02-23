package io.cloudflight.jems.server.programme.service.indicator.get_output_indicator

import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail

interface GetOutputIndicatorInteractor {
    fun getOutputIndicator(id: Long): OutputIndicatorDetail
}
