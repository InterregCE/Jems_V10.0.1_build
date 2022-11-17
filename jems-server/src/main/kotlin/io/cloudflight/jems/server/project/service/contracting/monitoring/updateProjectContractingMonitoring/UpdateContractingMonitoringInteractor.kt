package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring

interface UpdateContractingMonitoringInteractor {

    fun updateContractingMonitoring(
        projectId: Long,
        contractMonitoring: ProjectContractingMonitoring
    ): ProjectContractingMonitoring

}
