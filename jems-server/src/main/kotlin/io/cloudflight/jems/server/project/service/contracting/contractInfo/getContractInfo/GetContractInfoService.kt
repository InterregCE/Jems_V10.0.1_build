package io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo

import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.contractInfo.ProjectContractInfoPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractInfoService(
    private val projectContractInfoPersistence: ProjectContractInfoPersistence,
    private val getContractingMonitoringService: GetContractingMonitoringService,
    private val projectPersistence: ProjectPersistenceProvider,
) {

    @Transactional(readOnly = true)
    fun getContractInfo(projectId: Long): ProjectContractInfo {
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            ContractingValidator.validateProjectStepAndStatus(projectSummary)
        }
        val projectContractingMonitoring = getContractingMonitoringService.getProjectContractingMonitoring(projectId)
        return projectContractInfoPersistence.getContractInfo(projectId).also {
            it.projectStartDate = projectContractingMonitoring.startDate
            it.projectEndDate = projectContractingMonitoring.endDate
            it.subsidyContractDate =
                projectContractingMonitoring.addDates.maxByOrNull { addDate -> addDate.number }?.entryIntoForceDate
        }
    }
}
