package io.cloudflight.jems.server.project.service.contracting.monitoring.getLastApprovedPeriods

import io.cloudflight.jems.server.project.service.model.ProjectPeriod


interface GetLastApprovedPeriodsInteractor {

    fun getPeriods(projectId: Long): List<ProjectPeriod>

}
