package io.cloudflight.jems.server.project.service.contracting.monitoring.getLastApprovedPeriods

import io.cloudflight.jems.server.project.service.contracting.model.ProjectPeriodForMonitoring

interface GetLastApprovedPeriodsInteractor {

    fun getPeriods(projectId: Long): List<ProjectPeriodForMonitoring>

}
