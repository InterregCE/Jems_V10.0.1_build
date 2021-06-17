package io.cloudflight.jems.server.call.service.get_call

import io.cloudflight.jems.server.call.authorization.CanRetrieveCall
import io.cloudflight.jems.server.call.authorization.CanRetrieveCalls
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCall(private val persistence: CallPersistence) : GetCallInteractor {

    @CanRetrieveCalls
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallException::class)
    override fun getCalls(pageable: Pageable): Page<CallSummary> =
        persistence.getCalls(pageable)

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallException::class)
    override fun getPublishedCalls(pageable: Pageable): Page<CallSummary> =
        persistence.getPublishedAndOpenCalls(pageable)

    @CanRetrieveCall
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallException::class)
    override fun getCallById(callId: Long): CallDetail =
        persistence.getCallById(callId)

}
