package io.cloudflight.jems.server.call.service.get_allow_real_costs

import io.cloudflight.jems.server.call.service.model.AllowRealCosts

interface GetAllowRealCostsInteractor {
    fun getAllowRealCosts(callId: Long): AllowRealCosts
}
