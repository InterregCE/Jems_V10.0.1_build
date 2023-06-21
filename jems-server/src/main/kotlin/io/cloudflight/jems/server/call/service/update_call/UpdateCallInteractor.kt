package io.cloudflight.jems.server.call.service.update_call

import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallDetail

interface UpdateCallInteractor {

    fun updateCall(call: Call): CallDetail

}
