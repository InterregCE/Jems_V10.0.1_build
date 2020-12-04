package io.cloudflight.jems.server.programme.service.costoption.create_unit_cost

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

interface CreateUnitCostInteractor {

    fun createUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost

}
