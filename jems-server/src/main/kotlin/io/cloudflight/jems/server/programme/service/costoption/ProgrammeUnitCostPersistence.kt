package io.cloudflight.jems.server.programme.service.costoption

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProgrammeUnitCostPersistence {

    fun getUnitCosts(pageable: Pageable): Page<ProgrammeUnitCost>
    fun getUnitCost(unitCostId: Long): ProgrammeUnitCost
    fun createUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost
    fun updateUnitCost(unitCost: ProgrammeUnitCost): ProgrammeUnitCost
    fun deleteUnitCost(unitCostId: Long)

}
