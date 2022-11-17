package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectAvailableUnitCost

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectAvailableUnitCost(
    private val projectUnitCostPersistence: ProjectUnitCostPersistence,
) : GetProjectAvailableUnitCostInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetProjectAvailableUnitCostException::class)
    override fun getAvailableUnitCost(projectId: Long, version: String?) =
        projectUnitCostPersistence.getAvailableUnitCostsForProjectId(projectId, version)

}
