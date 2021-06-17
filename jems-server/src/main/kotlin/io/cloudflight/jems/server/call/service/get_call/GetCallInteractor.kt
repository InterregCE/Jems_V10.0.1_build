package io.cloudflight.jems.server.call.service.get_call

import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetCallInteractor {

    fun getCalls(pageable: Pageable): Page<CallSummary>

    fun getPublishedCalls(pageable: Pageable): Page<CallSummary>

    fun getCallById(callId: Long): CallDetail

}
