package io.cloudflight.jems.server.project.service.contracting.reporting

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule

interface ContractingReportingPersistence {

    fun getContractingReporting(projectId: Long): List<ProjectContractingReportingSchedule>

    fun getContractingReportingDeadline(projectId: Long, deadlineId: Long): ProjectContractingReportingSchedule

    fun updateContractingReporting(
        projectId: Long,
        deadlines: Collection<ProjectContractingReportingSchedule>,
    ): List<ProjectContractingReportingSchedule>

    fun clearPeriodAndDatesFor(ids: List<Long>)

    fun getScheduleIdsWhosePeriodsAndDatesNotProper(projectId: Long, newMaxDuration: Int): List<Long>

}
