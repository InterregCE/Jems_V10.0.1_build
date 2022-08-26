package io.cloudflight.jems.server.project.service.contracting.reporting.updateContractingReporting

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule

interface UpdateContractingReportingInteractor {
    fun updateReportingSchedule(projectId: Long, deadlines: Collection<ProjectContractingReportingSchedule>): List<ProjectContractingReportingSchedule>

    fun clearNoLongerAvailablePeriodsAndDates(projectId: Long, newMaxDuration: Int)
}
