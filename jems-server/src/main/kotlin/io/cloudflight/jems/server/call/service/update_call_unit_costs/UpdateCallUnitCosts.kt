package io.cloudflight.jems.server.call.service.update_call_unit_costs

import io.cloudflight.jems.server.call.authorization.CanUpdateCalls
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.callUpdated
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCallUnitCosts(
    private val persistence: CallPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : UpdateCallUnitCostsInteractor {

    @Transactional
    @CanUpdateCalls
    @ExceptionWrapper(UpdateCallUnitCostsExceptions::class)
    override fun updateUnitCosts(callId: Long, unitCostIds: Set<Long>): CallDetail {
        validateAllUnitCostsExists(unitCostIds) { persistence.existsAllProgrammeUnitCostsByIds(it) }

        val existingCall = persistence.getCallById(callId)
        validateUnitCostsNotRemovedIfCallPublished(existingCall, unitCostIds)

        return persistence.updateProjectCallUnitCost(callId, unitCostIds).also {
            auditPublisher.publishEvent(callUpdated(this, existingCall, it))
        }
    }

    private fun validateUnitCostsNotRemovedIfCallPublished(
        existingCall: CallDetail,
        newUnitCostIds: Set<Long>,
    ) {
        val existingUnitCostIds = existingCall.unitCosts.mapTo(HashSet()) { it.id }
        if (existingCall.isPublished() && !newUnitCostIds.containsAll(existingUnitCostIds))
            throw UnitCostsRemovedAfterCallPublished()
    }

    private fun validateAllUnitCostsExists(
        unitCostIds: Set<Long>,
        existsUnitCosts: (Set<Long>) -> Boolean
    ) {
        if (!existsUnitCosts.invoke(unitCostIds))
            throw UnitCostNotFound()
    }

}
