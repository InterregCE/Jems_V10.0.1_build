package io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting

import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetContractingReportingService(
    private val contractingReportingPersistence: ContractingReportingPersistence
) {

    @Transactional(readOnly = true)
    fun getReportingSchedule(projectId: Long) =
        contractingReportingPersistence.getContractingReporting(projectId)
}
