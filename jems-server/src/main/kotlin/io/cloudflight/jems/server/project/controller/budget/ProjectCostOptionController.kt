package io.cloudflight.jems.server.project.controller.budget

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.project.budget.ProjectCostOptionApi
import io.cloudflight.jems.server.programme.controller.costoption.toDetailDto
import io.cloudflight.jems.server.programme.controller.costoption.toDto
import io.cloudflight.jems.server.programme.controller.costoption.toModel
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.createProjectUnitCost.CreateProjectUnitCostInteractor
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.deleteProjectUnitCost.DeleteProjectUnitCostInteractor
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectAvailableUnitCost.GetProjectAvailableUnitCostInteractor
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.getProjectUnitCostList.GetProjectUnitCostListInteractor
import io.cloudflight.jems.server.project.service.customCostOptions.unitCost.updateProjectUnitCost.UpdateProjectUnitCostInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectCostOptionController(
    private val getProjectUnitCostListInteractor: GetProjectUnitCostListInteractor,
    private val createProjectUnitCostInteractor: CreateProjectUnitCostInteractor,
    private val updateProjectUnitCostInteractor: UpdateProjectUnitCostInteractor,
    private val deleteProjectUnitCostInteractor: DeleteProjectUnitCostInteractor,
    private val getProjectAvailableUnitCostInteractor: GetProjectAvailableUnitCostInteractor,
) : ProjectCostOptionApi {

    override fun getProjectAvailableUnitCosts(projectId: Long, version: String?) =
        getProjectAvailableUnitCostInteractor.getAvailableUnitCost(projectId, version).toDetailDto()

    override fun getProjectUnitCostList(projectId: Long, version: String?) =
        getProjectUnitCostListInteractor.getUnitCostList(projectId, version).toDto()

    override fun getProjectUnitCost(projectId: Long, unitCostId: Long, version: String?) =
        getProjectUnitCostListInteractor.getUnitCost(projectId, unitCostId = unitCostId, version).toDto()

    override fun createProjectUnitCost(projectId: Long, unitCost: ProgrammeUnitCostDTO) =
        createProjectUnitCostInteractor.createProjectUnitCost(projectId, unitCost.toModel()).toDto()

    override fun updateProjectUnitCost(projectId: Long, unitCost: ProgrammeUnitCostDTO) =
        updateProjectUnitCostInteractor.updateProjectUnitCost(projectId, unitCost.toModel()).toDto()

    override fun deleteProjectUnitCost(projectId: Long, unitCostId: Long) =
        deleteProjectUnitCostInteractor.deleteProjectUnitCost(projectId = projectId, unitCostId = unitCostId)

}
