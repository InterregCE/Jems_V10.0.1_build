package io.cloudflight.jems.server.project.service.contracting.management.updateProjectContractingManagement

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectManagement
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.management.ContractingManagementPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateContractingManagement(
    private val contractingManagementPersistence: ContractingManagementPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val validator: ContractingValidator
): UpdateContractingManagementInteractor {

    @CanEditProjectManagement
    @Transactional
    @ExceptionWrapper(UpdateContractingManagementException::class)
    override fun updateContractingManagement(projectId: Long , projectManagers: List<ProjectContractingManagement>): List<ProjectContractingManagement> {
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            validator.validateProjectStepAndStatus(projectSummary)
            validator.validateManagerContacts(projectManagers)
        }
        return contractingManagementPersistence.updateContractingManagement(projectManagers)
    }
}
