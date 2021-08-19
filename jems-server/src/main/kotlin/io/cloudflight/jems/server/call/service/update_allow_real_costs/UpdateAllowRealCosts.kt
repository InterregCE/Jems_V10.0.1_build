package io.cloudflight.jems.server.call.service.update_allow_real_costs

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.AllowRealCosts
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateAllowRealCosts(
    private val persistence: CallPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : UpdateAllowRealCostsInteractor {

    @Transactional
    @CanUpdateCall
    @ExceptionWrapper(UpdateAllowRealCostsExceptions::class)
    override fun updateAllowRealCosts(callId: Long, allowRealCosts: AllowRealCosts): AllowRealCosts =
        persistence.updateAllowRealCosts(callId, allowRealCosts)
}
