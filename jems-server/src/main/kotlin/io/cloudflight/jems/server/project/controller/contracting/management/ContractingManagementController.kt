package io.cloudflight.jems.server.project.controller.contracting.management

import io.cloudflight.jems.api.project.contracting.ContractingManagementApi
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingManagementDTO
import io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement.GetContractingManagementInteractor
import io.cloudflight.jems.server.project.service.contracting.management.updateProjectContractingManagement.UpdateContractingManagementInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingManagementController(
    private val updateContractingManagementInteractor: UpdateContractingManagementInteractor,
    private val getContractingManagementInteractor: GetContractingManagementInteractor
): ContractingManagementApi {

    override fun getContractingManagement(projectId: Long): List<ProjectContractingManagementDTO> {
        return getContractingManagementInteractor.getContractingManagement(projectId).toDTO()
    }

    override fun updateContractingManagement(
        projectId: Long,
        projectManagers: List<ProjectContractingManagementDTO>
    ): List<ProjectContractingManagementDTO> {
        return updateContractingManagementInteractor.updateContractingManagement(projectId, projectManagers.toModel()).toDTO()
    }
}
