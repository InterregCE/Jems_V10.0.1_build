package io.cloudflight.jems.server.call.service.update_call_unit_costs

import io.cloudflight.jems.server.call.service.model.CallDetail

interface UpdateCallUnitCostsInteractor {

    fun updateUnitCosts(callId: Long, unitCostIds: Set<Long>): CallDetail

}
