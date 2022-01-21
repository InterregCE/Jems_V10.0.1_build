package io.cloudflight.jems.server.call.service.publish_call

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.callPublished
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PublishCall(
    private val persistence: CallPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : PublishCallInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(PublishCallException::class)
    override fun publishCall(callId: Long): CallSummary =
        ifCallCanBePublished(callId).let {
            persistence.publishCall(callId).also {
                auditPublisher.publishEvent(callPublished(this, it)) }
        }

    private fun ifCallCanBePublished(callId: Long) =
        with(persistence.getCallById(callId)) {
            if (this.funds.isNullOrEmpty() || this.objectives.isNullOrEmpty() || this.preSubmissionCheckPluginKey.isNullOrBlank())
                throw CannotPublishCallException()
        }

    private fun isCallPublished(callId: Long) =
        persistence.isCallPublished(callId)

}
