package io.cloudflight.jems.server.project.repository.contracting.management

import io.cloudflight.jems.server.project.service.contracting.management.ContractingManagementPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingManagementPersistenceProvider(
    private val projectContractingManagementRepository: ProjectContractingManagementRepository
): ContractingManagementPersistence {

    @Transactional
    override fun getContractingManagement(projectId: Long): List<ProjectContractingManagement> {
        return projectContractingManagementRepository.findByManagementIdProjectId(projectId).toModelList()
    }

    @Transactional
    override fun updateContractingManagement(projectManagers: List<ProjectContractingManagement>): List<ProjectContractingManagement> {
        return projectContractingManagementRepository.saveAll(projectManagers.toEntities()).toModelList()
    }
}
