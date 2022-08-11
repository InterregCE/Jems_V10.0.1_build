package io.cloudflight.jems.server.project.service.contracting.reporting

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule

interface ContractingReportingPersistence {

    fun getContractingReporting(projectId: Long): List<ProjectContractingReportingSchedule>

    fun updateContractingReporting(
        projectId: Long,
        deadlines: Collection<ProjectContractingReportingSchedule>,
    ): List<ProjectContractingReportingSchedule>

}
