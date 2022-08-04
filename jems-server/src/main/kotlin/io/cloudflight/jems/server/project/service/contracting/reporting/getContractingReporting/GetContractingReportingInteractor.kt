package io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting

import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule

interface GetContractingReportingInteractor {
    fun getReportingSchedule(projectId: Long): List<ProjectContractingReportingSchedule>
}
