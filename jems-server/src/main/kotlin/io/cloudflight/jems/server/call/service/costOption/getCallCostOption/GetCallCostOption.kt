package io.cloudflight.jems.server.call.service.costOption.getCallCostOption

import io.cloudflight.jems.server.call.authorization.CanRetrieveCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCallCostOption(private val persistence: CallPersistence) : GetCallCostOptionInteractor {

    @CanRetrieveCall
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetCallCostOptionException::class)
    override fun getCallCostOption(callId: Long) =
        persistence.getCallCostOption(callId)

}
