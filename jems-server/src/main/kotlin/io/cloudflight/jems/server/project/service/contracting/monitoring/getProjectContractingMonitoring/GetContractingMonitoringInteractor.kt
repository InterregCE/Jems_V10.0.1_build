package io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring

interface GetContractingMonitoringInteractor {
    fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoring
}
