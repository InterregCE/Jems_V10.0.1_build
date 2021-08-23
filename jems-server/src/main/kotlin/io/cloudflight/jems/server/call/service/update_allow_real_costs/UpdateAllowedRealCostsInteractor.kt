package io.cloudflight.jems.server.call.service.update_allow_real_costs

import io.cloudflight.jems.server.call.service.model.AllowedRealCosts

interface UpdateAllowedRealCostsInteractor {
    fun updateAllowedRealCosts(callId: Long, allowedRealCosts: AllowedRealCosts): AllowedRealCosts
}
