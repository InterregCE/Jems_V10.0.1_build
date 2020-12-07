package io.cloudflight.jems.server.programme.service.costoption.delete_unit_cost

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUnitCost(
    private val persistence: ProgrammeUnitCostPersistence,
) : DeleteUnitCostInteractor {

    @CanUpdateProgrammeSetup
    @Transactional(readOnly = true)
    override fun deleteUnitCost(unitCostId: Long) =
        persistence.deleteUnitCost(unitCostId)

}
