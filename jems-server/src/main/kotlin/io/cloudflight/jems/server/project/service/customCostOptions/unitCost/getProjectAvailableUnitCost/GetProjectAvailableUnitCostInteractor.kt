package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectAvailableUnitCost

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost

interface GetProjectAvailableUnitCostInteractor {

    fun getAvailableUnitCost(projectId: Long, version: String? = null): List<ProgrammeUnitCost>

}
