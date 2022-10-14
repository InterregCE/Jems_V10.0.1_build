package io.cloudflight.jems.server.programme.service.costoption.deleteUnitCost

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.unitCostDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUnitCost(
    private val persistence: ProgrammeUnitCostPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val auditPublisher: ApplicationEventPublisher
) : DeleteUnitCostInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(DeleteUnitCostFailed::class)
    override fun deleteUnitCost(unitCostId: Long) {
        if (isProgrammeSetupLocked.isLocked())
            throw DeleteUnitCostWhenProgrammeSetupRestricted()

        if(persistence.getNumberOfOccurrencesInCalls(unitCostId) > 0){
            throw ToDeleteUnitCostAlreadyUsedInCall()
        }
        val unitCostToBeDeleted = persistence.getUnitCost(unitCostId)
        persistence.deleteUnitCost(unitCostId).also {
            auditPublisher.publishEvent(unitCostDeleted(this, unitCostToBeDeleted))
        }
    }
}
