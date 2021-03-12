package io.cloudflight.jems.server.call.service.get_call

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.authorization.CanReadCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCall(
    private val persistence: CallPersistence,
    private val securityService: SecurityService,
) : GetCallInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallException::class)
    override fun getCalls(pageable: Pageable): Page<CallSummary> =
        with(securityService.currentUser!!) {
            when {
                isAdmin || isProgrammeUser -> persistence.getCalls(pageable)
                isApplicant -> persistence.getPublishedAndOpenCalls(pageable)
                else -> Page.empty()
            }
        }

    @CanReadCall
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallException::class)
    override fun getCallById(callId: Long): CallDetail =
        persistence.getCallById(callId)

}
