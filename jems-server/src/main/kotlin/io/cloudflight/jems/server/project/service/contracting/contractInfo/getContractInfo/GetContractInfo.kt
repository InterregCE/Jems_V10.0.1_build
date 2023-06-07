package io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewContractsAndAgreements
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.contractInfo.ProjectContractInfoPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractInfo(
    private val getContractInfoService: GetContractInfoService,
): GetContractInfoInteractor {

    @CanViewContractsAndAgreements
    @ExceptionWrapper(GetContractInfoException::class)
    override fun getContractInfo(projectId: Long): ProjectContractInfo = getContractInfoService.getContractInfo(projectId)
}
