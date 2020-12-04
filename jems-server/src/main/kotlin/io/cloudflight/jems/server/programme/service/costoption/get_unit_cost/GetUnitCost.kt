package io.cloudflight.jems.server.programme.service.costoption.get_unit_cost

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUnitCost(
    private val persistence: ProgrammeUnitCostPersistence,
) : GetUnitCostInteractor {

    @CanUpdateProgrammeSetup
    @Transactional(readOnly = true)
    override fun getUnitCosts(pageable: Pageable): Page<ProgrammeUnitCost> =
        persistence.getUnitCosts(pageable)

    @CanUpdateProgrammeSetup
    @Transactional(readOnly = true)
    override fun getUnitCost(unitCostId: Long): ProgrammeUnitCost =
        persistence.getUnitCost(unitCostId)

}
