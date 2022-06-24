package io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectManagement
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.contracting.management.ContractingManagementPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingManagement(
    private val contractingManagementPersistence: ContractingManagementPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val validator: ContractingValidator,
    ): GetContractingManagementInteractor {

    @CanViewProjectManagement
    @Transactional
    @ExceptionWrapper(GetContractingManagementException::class)
    override fun getContractingManagement(projectId: Long): List<ProjectContractingManagement> {
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            validator.validateProjectStepAndStatus(projectSummary)
        }
        return contractingManagementPersistence.getContractingManagement(projectId)
    }

}
