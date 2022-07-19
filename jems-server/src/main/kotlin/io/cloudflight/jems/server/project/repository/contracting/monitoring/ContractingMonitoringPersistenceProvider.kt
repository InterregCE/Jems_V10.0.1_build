package io.cloudflight.jems.server.project.repository.contracting.monitoring

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingMonitoringPersistenceProvider(
    private val projectContractingMonitoringRepository: ProjectContractingMonitoringRepository
): ContractingMonitoringPersistence {

    @Transactional
    override fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoring {
        return projectContractingMonitoringRepository
            .findByProjectId(projectId)
            .let {
                when {
                    it.isPresent -> it.get().toModel()
                    else -> ProjectContractingMonitoring(projectId = projectId, addDates = emptyList())
                }
            }
    }

    @Transactional
    override fun updateContractingMonitoring(contractMonitoring: ProjectContractingMonitoring): ProjectContractingMonitoring {
        return projectContractingMonitoringRepository.save(contractMonitoring.toEntity()).toModel()
    }
}
