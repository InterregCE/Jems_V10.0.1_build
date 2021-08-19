package io.cloudflight.jems.server.call.service.get_allow_real_costs

import io.cloudflight.jems.server.call.authorization.CanRetrieveCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.AllowRealCosts
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAllowRealCosts(private val persistence: CallPersistence) : GetAllowRealCostsInteractor {

    @Transactional
    @CanRetrieveCall
    @ExceptionWrapper(GetAllowRealCostsExceptions::class)
    override fun getAllowRealCosts(callId: Long): AllowRealCosts = persistence.getAllowRealCosts(callId)
}
