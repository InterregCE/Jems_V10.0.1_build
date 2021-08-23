package io.cloudflight.jems.server.call.service.get_allow_real_costs

import io.cloudflight.jems.server.call.service.model.AllowedRealCosts

interface GetAllowedRealCostsInteractor {
    fun getAllowedRealCosts(callId: Long): AllowedRealCosts
}
