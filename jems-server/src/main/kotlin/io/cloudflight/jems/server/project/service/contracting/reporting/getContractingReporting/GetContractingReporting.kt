package io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectContractingReporting
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingReporting(
    private val contractingReportingPersistence: ContractingReportingPersistence,
): GetContractingReportingInteractor {

    @CanRetrieveProjectContractingReporting
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetContractingReportingException::class)
    override fun getReportingSchedule(projectId: Long) =
        contractingReportingPersistence.getContractingReporting(projectId)

}
