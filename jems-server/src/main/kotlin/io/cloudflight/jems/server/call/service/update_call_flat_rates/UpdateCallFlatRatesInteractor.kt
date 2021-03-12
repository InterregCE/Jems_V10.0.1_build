package io.cloudflight.jems.server.call.service.update_call_flat_rates

import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate

interface UpdateCallFlatRatesInteractor {

    fun updateFlatRateSetup(callId: Long, flatRates: Set<ProjectCallFlatRate>): CallDetail

}
