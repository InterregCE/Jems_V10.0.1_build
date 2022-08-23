package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.updateProjectUnitCost

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

interface UpdateProjectUnitCostInteractor {

    fun updateProjectUnitCost(projectId: Long, unitCost: ProgrammeUnitCost): ProgrammeUnitCost

}
