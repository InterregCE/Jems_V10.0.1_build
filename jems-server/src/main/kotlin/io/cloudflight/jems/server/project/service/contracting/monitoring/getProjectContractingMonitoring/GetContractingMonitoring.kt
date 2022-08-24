package io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingMonitoring(
    private val getContractingMonitoringService: GetContractingMonitoringService,
): GetContractingMonitoringInteractor {

    @CanRetrieveProjectContractingMonitoring
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetContractingMonitoringException::class)
    override fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoring =
        getContractingMonitoringService.getContractingMonitoring(projectId)

}
