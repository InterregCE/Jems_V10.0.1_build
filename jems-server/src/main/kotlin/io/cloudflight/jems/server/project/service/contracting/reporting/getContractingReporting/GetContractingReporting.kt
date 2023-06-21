package io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReportingSchedule
import org.springframework.stereotype.Service

@Service
class GetContractingReporting(
    private val getContractingReportingService: GetContractingReportingService,
): GetContractingReportingInteractor {

    @CanRetrieveProjectReportingSchedule
    @ExceptionWrapper(GetContractingReportingException::class)
    override fun getReportingSchedule(projectId: Long) =
        getContractingReportingService.getReportingSchedule(projectId)

}
