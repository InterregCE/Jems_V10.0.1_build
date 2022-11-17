package io.cloudflight.jems.server.project.service.contracting.management.updateProjectContractingManagement

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement

interface UpdateContractingManagementInteractor {
    fun updateContractingManagement(projectId: Long, projectManagers: List<ProjectContractingManagement>): List<ProjectContractingManagement>
}
