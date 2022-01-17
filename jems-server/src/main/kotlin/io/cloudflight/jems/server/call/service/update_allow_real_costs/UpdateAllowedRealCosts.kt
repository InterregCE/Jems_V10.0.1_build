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
    override fun updateAllowedRealCosts(callId: Long, allowedRealCosts: AllowedRealCosts): AllowedRealCosts {
        if (persistence.isCallPublished(callId) && !this.canBeUpdated(persistence.getAllowedRealCosts(callId), allowedRealCosts)) {
                throw CallNotEditableException()
        }
        return persistence.updateAllowedRealCosts(callId, allowedRealCosts)
    }

    fun canBeUpdated(databaseObject: AllowedRealCosts?, callObject: AllowedRealCosts?): Boolean {
        if (databaseObject == null || callObject == null)
            return false

        return !isCheckedCostUpdated(databaseObject.allowRealEquipmentCosts, callObject.allowRealEquipmentCosts)
            && !isCheckedCostUpdated(databaseObject.allowRealTravelAndAccommodationCosts, callObject.allowRealTravelAndAccommodationCosts)
            && !isCheckedCostUpdated(databaseObject.allowRealExternalExpertiseAndServicesCosts, callObject.allowRealExternalExpertiseAndServicesCosts)
            && !isCheckedCostUpdated(databaseObject.allowRealEquipmentCosts, callObject.allowRealEquipmentCosts)
            && !isCheckedCostUpdated(databaseObject.allowRealInfrastructureCosts, callObject.allowRealInfrastructureCosts)
    }

    fun isCheckedCostUpdated(savedRealCost: Boolean, callRealCost: Boolean): Boolean =
         savedRealCost && savedRealCost != callRealCost
}
