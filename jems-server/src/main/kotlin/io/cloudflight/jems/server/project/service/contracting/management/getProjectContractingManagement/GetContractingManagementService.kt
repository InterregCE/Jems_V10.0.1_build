package io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement

import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.management.ContractingManagementPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingManagementService(
    private val contractingManagementPersistence: ContractingManagementPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
) {

    @Transactional(readOnly = true)
    fun getContractingManagement(projectId: Long): List<ProjectContractingManagement> {
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            ContractingValidator.validateProjectStepAndStatus(projectSummary)
        }
        return contractingManagementPersistence.getContractingManagement(projectId)
    }
}
