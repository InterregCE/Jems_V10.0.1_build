package io.cloudflight.jems.server.programme.service.costoption.update_unit_cost

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

interface UpdateUnitCostInteractor {

    fun updateUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost

}
