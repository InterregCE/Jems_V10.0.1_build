package io.cloudflight.jems.server.call.service.update_call_lump_sums

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.callUpdated
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCallLumpSums(
    private val persistence: CallPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : UpdateCallLumpSumsInteractor {

    @Transactional
    @CanUpdateCall
    @ExceptionWrapper(UpdateCallLumpSumsException::class)
    override fun updateLumpSums(callId: Long, lumpSumIds: Set<Long>): CallDetail {
        validateAllLumpSumsExists(lumpSumIds) { persistence.existsAllProgrammeLumpSumsByIds(it) }

        val existingCall = persistence.getCallById(callId)
        validateLumpSumsNotRemovedIfCallPublished(existingCall, lumpSumIds)

        return persistence.updateProjectCallLumpSum(callId, lumpSumIds).also {
            auditPublisher.publishEvent(callUpdated(this, existingCall, it))
        }
    }

    private fun validateLumpSumsNotRemovedIfCallPublished(
        existingCall: CallDetail,
        newLumpSumIds: Set<Long>,
    ) {
        val existingLumpSumIds = existingCall.lumpSums.mapTo(HashSet()) { it.id }
        if (existingCall.isPublished() && !newLumpSumIds.containsAll(existingLumpSumIds))
            throw LumpSumsRemovedAfterCallPublished()
    }

    private fun validateAllLumpSumsExists(
        lumpSumIds: Set<Long>,
        existsLumpSums: (Set<Long>) -> Boolean
    ) {
        if (!existsLumpSums.invoke(lumpSumIds))
            throw LumpSumNotFound()
    }

}
