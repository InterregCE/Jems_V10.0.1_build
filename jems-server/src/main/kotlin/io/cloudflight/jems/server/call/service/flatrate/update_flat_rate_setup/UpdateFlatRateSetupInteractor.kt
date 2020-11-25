package io.cloudflight.jems.server.call.service.flatrate.update_flat_rate_setup

import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate

interface UpdateFlatRateSetupInteractor {
    fun updateFlatRateSetup(callId: Long, flatRates: Set<ProjectCallFlatRate>)
}
