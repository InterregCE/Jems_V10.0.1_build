package io.cloudflight.jems.server.project.service.contracting.monitoring

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring

interface ContractingMonitoringPersistence {

    fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoring

    fun updateContractingMonitoring(contractMonitoring: ProjectContractingMonitoring): ProjectContractingMonitoring

    fun existsSavedInstallment(projectId: Long, lumpSumId: Long, orderNr: Int): Boolean

}
