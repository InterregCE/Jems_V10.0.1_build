package io.cloudflight.jems.server.project.controller.contracting.monitoring

import io.cloudflight.jems.api.project.contracting.ContractingMonitoringApi
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringDTO
import io.cloudflight.jems.server.project.service.contracting.monitoring.getLastApprovedPeriods.GetLastApprovedPeriodsInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring.UpdateContractingMonitoringInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingMonitoringController(
    private val getContractingMonitoringInteractor: GetContractingMonitoringInteractor,
    private val updateContractingMonitoringInteractor: UpdateContractingMonitoringInteractor,
    private val getLastApprovedPeriodsInteractor: GetLastApprovedPeriodsInteractor,
): ContractingMonitoringApi {

    override fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoringDTO {
        return getContractingMonitoringInteractor.getContractingMonitoring(projectId).toDTO()
    }

    override fun updateContractingMonitoring(
        projectId: Long,
        contractingMonitoring: ProjectContractingMonitoringDTO
    ): ProjectContractingMonitoringDTO {
        return updateContractingMonitoringInteractor
            .updateContractingMonitoring(projectId, contractingMonitoring.toModel()).toDTO()
    }

    override fun getContractingMonitoringPeriods(projectId: Long) =
        getLastApprovedPeriodsInteractor.getPeriods(projectId).toDTO()

}
