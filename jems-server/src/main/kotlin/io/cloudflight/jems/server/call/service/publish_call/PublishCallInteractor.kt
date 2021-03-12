package io.cloudflight.jems.server.call.service.publish_call

import io.cloudflight.jems.server.call.service.model.CallSummary

interface PublishCallInteractor {

    fun publishCall(callId: Long): CallSummary

}
