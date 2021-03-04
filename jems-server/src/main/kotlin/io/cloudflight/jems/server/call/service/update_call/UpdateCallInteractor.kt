package io.cloudflight.jems.server.call.service.update_call

import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call

interface UpdateCallInteractor {

    fun updateCall(call: Call): CallDetail

}
