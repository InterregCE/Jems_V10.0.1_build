package io.cloudflight.jems.server.call.service.update_call_lump_sums

import io.cloudflight.jems.server.call.service.model.CallDetail

interface UpdateCallLumpSumsInteractor {

    fun updateLumpSums(callId: Long, lumpSumIds: Set<Long>): CallDetail

}
