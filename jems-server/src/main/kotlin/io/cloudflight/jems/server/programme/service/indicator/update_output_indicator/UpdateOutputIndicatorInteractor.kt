package io.cloudflight.jems.server.programme.service.indicator.update_output_indicator

import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail

interface UpdateOutputIndicatorInteractor {
    fun updateOutputIndicator(outputIndicator: OutputIndicator): OutputIndicatorDetail
}
