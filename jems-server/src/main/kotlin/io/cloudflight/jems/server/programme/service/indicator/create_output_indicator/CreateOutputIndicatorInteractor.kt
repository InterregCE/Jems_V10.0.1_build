package io.cloudflight.jems.server.programme.service.indicator.create_output_indicator

import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail

interface CreateOutputIndicatorInteractor {
    fun createOutputIndicator(outputIndicator: OutputIndicator): OutputIndicatorDetail
}
