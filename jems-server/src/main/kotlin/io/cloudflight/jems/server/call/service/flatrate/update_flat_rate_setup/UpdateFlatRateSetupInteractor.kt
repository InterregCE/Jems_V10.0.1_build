package io.cloudflight.jems.server.call.service.flatrate.update_flat_rate_setup

import io.cloudflight.jems.api.call.dto.flatrate.InputCallFlatRateSetup

interface UpdateFlatRateSetupInteractor {
    fun updateFlatRateSetup(callId: Long, flatRates: Set<InputCallFlatRateSetup>)
}
