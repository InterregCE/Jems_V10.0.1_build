package io.cloudflight.jems.server.call.service.create_call

import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call

interface CreateCallInteractor {

    fun createCallInDraft(call: Call): CallDetail

}
