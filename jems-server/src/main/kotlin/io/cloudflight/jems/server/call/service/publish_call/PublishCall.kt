package io.cloudflight.jems.server.call.service.publish_call

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.authorization.CanUpdateCalls
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.callPublished
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PublishCall(
    private val persistence: CallPersistence,
    private val auditService: AuditService,
) : PublishCallInteractor {

    @CanUpdateCalls
    @Transactional
    @ExceptionWrapper(PublishCallException::class)
    override fun publishCall(callId: Long): CallSummary =
        persistence.publishCall(callId).also { callPublished(it).logWith(auditService) }

}
