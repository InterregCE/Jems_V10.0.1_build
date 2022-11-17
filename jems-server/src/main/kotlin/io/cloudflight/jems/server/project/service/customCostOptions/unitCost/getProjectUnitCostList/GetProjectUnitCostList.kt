package io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectUnitCostList

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.customCostOptions.ProjectUnitCostPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectUnitCostList(
    private val projectUnitCostPersistence: ProjectUnitCostPersistence,
) : GetProjectUnitCostListInteractor {

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetProjectUnitCostListException::class)
    override fun getUnitCostList(projectId: Long, version: String?) =
        projectUnitCostPersistence.getProjectUnitCostList(projectId, version)

    @Transactional(readOnly = true)
    @CanRetrieveProjectForm
    @ExceptionWrapper(GetProjectUnitCostDetailException::class)
    override fun getUnitCost(projectId: Long, unitCostId: Long, version: String?) =
        projectUnitCostPersistence.getProjectUnitCost(projectId, unitCostId, version)

}
