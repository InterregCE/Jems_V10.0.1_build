package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectUnitCostList

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

interface GetProjectUnitCostListInteractor {

    fun getUnitCostList(projectId: Long, version: String? = null): List<ProgrammeUnitCost>

    fun getUnitCost(projectId: Long, unitCostId: Long, version: String? = null): ProgrammeUnitCost

}
