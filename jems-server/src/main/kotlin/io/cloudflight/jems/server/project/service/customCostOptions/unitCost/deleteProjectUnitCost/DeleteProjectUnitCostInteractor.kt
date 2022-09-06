package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.deleteProjectUnitCost

interface DeleteProjectUnitCostInteractor {

    fun deleteProjectUnitCost(projectId: Long, unitCostId: Long)

}
