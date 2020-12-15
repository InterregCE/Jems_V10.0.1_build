package io.cloudflight.jems.server.call.service.costoption.update_call_cost_options

interface UpdateCallCostOptionsInteractor {

    fun updateLumpSums(callId: Long, lumpSumIds: Set<Long>)

    fun updateUnitCosts(callId: Long, unitCostIds: Set<Long>)

}
