package io.cloudflight.jems.server.call.service.flatrate

import io.cloudflight.jems.server.call.service.flatrate.model.FlatRateModel

interface CallFlatRateSetupPersistence {

    fun updateFlatRateSetup(callId: Long, flatRates: Set<FlatRateModel>)

    fun getFlatRateSetup(callId: Long): Set<FlatRateModel>

}
