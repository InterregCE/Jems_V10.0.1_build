package io.cloudflight.jems.server.programme.service.costoption.get_unit_cost

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetUnitCostInteractor {

    fun getUnitCosts(): List<ProgrammeUnitCost>

    fun getUnitCost(unitCostId: Long): ProgrammeUnitCost

}
