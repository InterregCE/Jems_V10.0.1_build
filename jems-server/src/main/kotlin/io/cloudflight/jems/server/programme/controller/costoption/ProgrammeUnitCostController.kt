package io.cloudflight.jems.server.programme.controller.costoption

import io.cloudflight.jems.api.programme.costoption.ProgrammeUnitCostApi
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostListDTO
import io.cloudflight.jems.server.programme.service.costoption.create_unit_cost.CreateUnitCostInteractor
import io.cloudflight.jems.server.programme.service.costoption.deleteUnitCost.DeleteUnitCostInteractor
import io.cloudflight.jems.server.programme.service.costoption.get_unit_cost.GetUnitCostInteractor
import io.cloudflight.jems.server.programme.service.costoption.update_unit_cost.UpdateUnitCostInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgrammeUnitCostController(
    private val getUnitCost: GetUnitCostInteractor,
    private val createUnitCost: CreateUnitCostInteractor,
    private val updateUnitCost: UpdateUnitCostInteractor,
    private val deleteUnitCost: DeleteUnitCostInteractor,
) : ProgrammeUnitCostApi {

    override fun getProgrammeUnitCosts(): List<ProgrammeUnitCostListDTO> =
        getUnitCost.getUnitCosts().toDto()

    override fun getProgrammeUnitCost(unitCostId: Long): ProgrammeUnitCostDTO =
        getUnitCost.getUnitCost(unitCostId).toDto()

    override fun createProgrammeUnitCost(unitCost: ProgrammeUnitCostDTO): ProgrammeUnitCostDTO =
        createUnitCost.createUnitCost(unitCost.toModel()).toDto()

    override fun updateProgrammeUnitCost(unitCost: ProgrammeUnitCostDTO): ProgrammeUnitCostDTO =
        updateUnitCost.updateUnitCost(unitCost.toModel()).toDto()

    override fun deleteProgrammeUnitCost(unitCostId: Long) =
        deleteUnitCost.deleteUnitCost(unitCostId)


}
