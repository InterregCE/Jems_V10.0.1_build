package io.cloudflight.jems.server.call.service.update_allow_real_costs

import io.cloudflight.jems.server.call.service.model.AllowRealCosts

interface UpdateAllowRealCostsInteractor {
    fun updateAllowRealCosts(callId: Long, allowRealCosts: AllowRealCosts): AllowRealCosts
}
