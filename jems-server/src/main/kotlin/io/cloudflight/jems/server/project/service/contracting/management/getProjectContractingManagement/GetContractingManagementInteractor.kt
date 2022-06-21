package io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement

interface GetContractingManagementInteractor {
    fun getContractingManagement(projectId: Long): List<ProjectContractingManagement>
}
