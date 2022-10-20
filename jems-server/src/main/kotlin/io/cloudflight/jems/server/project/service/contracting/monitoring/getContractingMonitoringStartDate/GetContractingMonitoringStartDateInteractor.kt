package io.cloudflight.jems.server.project.service.contracting.monitoring.getContractingMonitoringStartDate

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringStartDate

interface GetContractingMonitoringStartDateInteractor {

    fun getStartDate(projectId: Long): ProjectContractingMonitoringStartDate

}
