package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

interface ProgrammeUnitCostPersistence {

    fun getUnitCosts(): List<ProgrammeUnitCost>
    fun getUnitCost(unitCostId: Long): ProgrammeUnitCost
    fun getCount(): Long
    fun createUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost
    fun updateUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost
    fun deleteUnitCost(unitCostId: Long)

}
