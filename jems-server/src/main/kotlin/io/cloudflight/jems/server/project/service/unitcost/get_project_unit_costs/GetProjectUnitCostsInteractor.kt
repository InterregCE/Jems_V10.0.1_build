package io.cloudflight.jems.server.project.service.unitcost.get_project_unit_costs

import io.cloudflight.jems.server.project.service.unitcost.model.ProjectUnitCost

interface GetProjectUnitCostsInteractor {
    fun getProjectUnitCost(projectId: Long, version: String? = null): List<ProjectUnitCost>
}