package io.cloudflight.jems.server.call.service.costOption.updateCallCostOption

import io.cloudflight.jems.server.call.service.model.CallCostOption

interface UpdateCallCostOptionInteractor {

    fun updateCallCostOption(callId: Long, costOption: CallCostOption): CallCostOption

}
