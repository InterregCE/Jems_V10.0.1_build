package io.cloudflight.jems.server.call.service.costOption.updateCallCostOption

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallCostOption
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCallCostOption(private val persistence: CallPersistence) : UpdateCallCostOptionInteractor {

    @CanUpdateCall
    @Transactional
    @ExceptionWrapper(UpdateCallCostOptionException::class)
    override fun updateCallCostOption(callId: Long, costOption: CallCostOption): CallCostOption {
        if (persistence.getCallById(callId).isPublished()) {
            validateProjectDefinedCostOptionNotDeselected(callId, costOption)
        }

        return persistence.updateCallCostOption(callId, costOption)
    }

    private fun validateProjectDefinedCostOptionNotDeselected(callId: Long, newOptions: CallCostOption) {
        val oldOptions = persistence.getCallCostOption(callId)

        val unitCostDeselected = oldOptions.projectDefinedUnitCostAllowed && newOptions.projectDefinedUnitCostAllowed.not()
        val lumpSumDeselected = oldOptions.projectDefinedLumpSumAllowed && newOptions.projectDefinedLumpSumAllowed.not()

        if (unitCostDeselected || lumpSumDeselected) {
            throw CallNotEditableException()
        }
    }

}
