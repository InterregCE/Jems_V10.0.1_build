package io.cloudflight.jems.server.call.service.update_allow_real_costs

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.AllowedRealCosts
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAllowedRealCosts(private val persistence: CallPersistence) : UpdateAllowedRealCostsInteractor {

    @Transactional
    @CanUpdateCall
    @ExceptionWrapper(UpdateAllowedRealCostsExceptions::class)
    override fun updateAllowedRealCosts(callId: Long, allowedRealCosts: AllowedRealCosts): AllowedRealCosts =
        persistence.updateAllowedRealCosts(callId, allowedRealCosts)
}
