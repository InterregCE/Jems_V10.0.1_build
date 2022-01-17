package io.cloudflight.jems.server.project.service.unitcost.get_project_unit_costs

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.budget.ProjectBudgetPersistence
import io.cloudflight.jems.server.project.service.unitcost.model.ProjectUnitCost
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectUnitCosts(
    private val projectBudgetPersistence: ProjectBudgetPersistence
) : GetProjectUnitCostsInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetProjectUnitCostsException::class)
    override fun getProjectUnitCost(projectId: Long,  version: String?): List<ProjectUnitCost> {
        return projectBudgetPersistence.getProjectUnitCosts(projectId, version)
    }
}