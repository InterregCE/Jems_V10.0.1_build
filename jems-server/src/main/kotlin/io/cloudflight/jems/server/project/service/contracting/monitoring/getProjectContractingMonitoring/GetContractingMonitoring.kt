package io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingMonitoring
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.fillEndDateWithDuration
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingMonitoring(
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val versionPersistence: ProjectVersionPersistence,
    private val validator: ContractingValidator
): GetContractingMonitoringInteractor {

    @CanRetrieveProjectContractingMonitoring
    @Transactional
    @ExceptionWrapper(GetContractingMonitoringException::class)
    override fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoring {
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            validator.validateProjectStatusForModification(projectSummary)
        }

        return contractingMonitoringPersistence.getContractingMonitoring(projectId)
            .fillEndDateWithDuration(resolveDuration = {
                versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
                    .let { projectPersistence.getProject(projectId = projectId, version = it).duration }
            })
    }

}
