package io.cloudflight.jems.server.call.service.update_call_flat_rates

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.authorization.CanUpdateCalls
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.callUpdated
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCallFlatRates(
    private val persistence: CallPersistence,
    private val auditService: AuditService,
) : UpdateCallFlatRatesInteractor {

    @Transactional
    @CanUpdateCalls
    @ExceptionWrapper(UpdateCallFlatRatesExceptions::class)
    override fun updateFlatRateSetup(callId: Long, flatRates: Set<ProjectCallFlatRate>): CallDetail {
        validateFlatRates(flatRates)

        val existingCall = persistence.getCallById(callId)
        validateFlatRatesNotRemovedNorChangedIfPublished(existingCall, flatRates)

        return persistence.updateProjectCallFlatRate(callId, flatRates).also {
            callUpdated(existingCall, it).logWith(auditService)
        }
    }

    private fun validateFlatRatesNotRemovedNorChangedIfPublished(
        existingCall: CallDetail,
        newFlatRates: Set<ProjectCallFlatRate>,
    ) {
        val removed = existingCall.flatRates.filterTo(HashSet()) { !newFlatRates.contains(it) }
        if(existingCall.isPublished() && removed.isNotEmpty())
            throw FlatRatesRemovedAfterCallPublished(removed.mapTo(HashSet()) { it.type })
    }

}
