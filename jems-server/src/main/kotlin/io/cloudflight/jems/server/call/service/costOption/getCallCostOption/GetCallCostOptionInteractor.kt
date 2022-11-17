package io.cloudflight.jems.server.call.service.costOption.getCallCostOption

import io.cloudflight.jems.server.call.service.model.CallCostOption

interface GetCallCostOptionInteractor {

    fun getCallCostOption(callId: Long): CallCostOption

}
