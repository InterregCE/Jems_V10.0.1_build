package io.cloudflight.jems.server.programme.service.costoption.delete_unit_cost

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.DeleteUnitCostWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUnitCost(
    private val persistence: ProgrammeUnitCostPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
) : DeleteUnitCostInteractor {

    @CanUpdateProgrammeSetup
    @Transactional(readOnly = true)
    override fun deleteUnitCost(unitCostId: Long) {
        if (isProgrammeSetupLocked.isLocked())
            throw DeleteUnitCostWhenProgrammeSetupRestricted()
        persistence.deleteUnitCost(unitCostId)
    }

}
