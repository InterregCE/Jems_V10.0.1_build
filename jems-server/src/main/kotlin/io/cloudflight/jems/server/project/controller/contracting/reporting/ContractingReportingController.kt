package io.cloudflight.jems.server.project.controller.contracting.reporting

import io.cloudflight.jems.api.project.contracting.ContractingReportingApi
import io.cloudflight.jems.api.project.dto.contracting.reporting.ProjectContractingReportingScheduleDTO
import io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting.GetContractingReportingInteractor
import io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting.UpdateContractingReportingInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractingReportingController(
    private val getContractingReportingInteractor: GetContractingReportingInteractor,
    private val updateContractingReportingInteractor: UpdateContractingReportingInteractor,
): ContractingReportingApi {

    override fun getReportingSchedule(projectId: Long) =
        getContractingReportingInteractor.getReportingSchedule(projectId).toDto()

    override fun updateReportingSchedule(projectId: Long, deadlines: List<ProjectContractingReportingScheduleDTO>) =
        updateContractingReportingInteractor.updateReportingSchedule(projectId, deadlines.toModel()).toDto()

}
