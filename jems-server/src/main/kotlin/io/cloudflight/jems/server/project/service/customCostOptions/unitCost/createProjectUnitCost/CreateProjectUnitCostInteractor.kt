package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.createProjectUnitCost

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

interface CreateProjectUnitCostInteractor {

    fun createProjectUnitCost(projectId: Long, unitCost: ProgrammeUnitCost): ProgrammeUnitCost

}
