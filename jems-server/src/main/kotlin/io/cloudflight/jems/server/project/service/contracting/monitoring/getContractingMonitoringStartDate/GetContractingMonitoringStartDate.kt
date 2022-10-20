package io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingReporting
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringStartDate
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingMonitoringStartDate(
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
): GetContractingMonitoringStartDateInteractor {

    @CanRetrieveProjectContractingReporting
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetContractingMonitoringStartDateException::class)
    override fun getStartDate(projectId: Long): ProjectContractingMonitoringStartDate {
        return ProjectContractingMonitoringStartDate(contractingMonitoringPersistence.getContractingMonitoring(projectId).startDate)
    }
}
