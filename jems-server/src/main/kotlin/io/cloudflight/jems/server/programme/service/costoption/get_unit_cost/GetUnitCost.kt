package io.cloudflight.jems.server.programme.service.costoption.get_unit_cost

import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUnitCost(
    private val persistence: ProgrammeUnitCostPersistence,
) : GetUnitCostInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    override fun getUnitCosts(): List<ProgrammeUnitCost> =
        persistence.getUnitCosts()

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    override fun getUnitCost(unitCostId: Long): ProgrammeUnitCost =
        persistence.getUnitCost(unitCostId)

}
