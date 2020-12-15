package io.cloudflight.jems.server.call.service.costoption.update_call_cost_options

import io.cloudflight.jems.server.call.authorization.CanUpdateCall
import io.cloudflight.jems.server.call.service.costoption.CallCostOptionsPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateCallCostOptions(private val persistence: CallCostOptionsPersistence) : UpdateCallCostOptionsInteractor {

    @Transactional
    @CanUpdateCall
    override fun updateLumpSums(callId: Long, lumpSumIds: Set<Long>) =
        persistence.updateProjectCallLumpSum(callId, lumpSumIds)

    @Transactional
    @CanUpdateCall
    override fun updateUnitCosts(callId: Long, unitCostIds: Set<Long>) =
        persistence.updateProjectCallUnitCost(callId, unitCostIds)

}
