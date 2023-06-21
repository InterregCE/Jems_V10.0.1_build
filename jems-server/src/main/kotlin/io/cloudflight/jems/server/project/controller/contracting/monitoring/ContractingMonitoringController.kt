package io.cloudflight.jems.server.project.controller.contracting.monitoring

import io.cloudflight.jems.api.project.contracting.ContractingMonitoringApi
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringDTO
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingMonitoringStartDateDTO
import io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringProjectBudget.GetContractingMonitoringProjectBudgetInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate.GetContractingMonitoringStartDateInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.getLastApprovedPeriods.GetLastApprovedPeriodsInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringInteractor
import io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring.UpdateContractingMonitoringInteractor
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class ContractingMonitoringController(
    private val getContractingMonitoringInteractor: GetContractingMonitoringInteractor,
    private val updateContractingMonitoringInteractor: UpdateContractingMonitoringInteractor,
    private val getLastApprovedPeriodsInteractor: GetLastApprovedPeriodsInteractor,
    private val getContractingMonitoringStartDateInteractor: GetContractingMonitoringStartDateInteractor,
    private val getContractingMonitoringProjectBudgetInteractor: GetContractingMonitoringProjectBudgetInteractor,
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

    override fun getContractingMonitoringStartDate(projectId: Long): ProjectContractingMonitoringStartDateDTO {
        return getContractingMonitoringStartDateInteractor.getStartDate(projectId).toDTO()
    }

    override fun getContractingMonitoringProjectBudget(projectId: Long, version: String?): BigDecimal {
        return getContractingMonitoringProjectBudgetInteractor.getProjectBudget(projectId, version)
    }

}
