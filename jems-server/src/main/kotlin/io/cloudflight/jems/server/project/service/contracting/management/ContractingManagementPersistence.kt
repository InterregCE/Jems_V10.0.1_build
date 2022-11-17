package io.cloudflight.jems.server.project.service.contracting.management

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement

interface ContractingManagementPersistence {

    fun getContractingManagement(projectId: Long): List<ProjectContractingManagement>

    fun updateContractingManagement(projectManagers: List<ProjectContractingManagement>): List<ProjectContractingManagement>
}
