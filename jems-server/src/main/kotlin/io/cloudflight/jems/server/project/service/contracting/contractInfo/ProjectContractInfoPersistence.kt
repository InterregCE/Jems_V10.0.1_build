package io.cloudflight.jems.server.project.service.contracting.contractInfo

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo

interface ProjectContractInfoPersistence {

    fun getContractInfo(projectId: Long): ProjectContractInfo

    fun updateContractInfo(projectId: Long, contractInfo: ProjectContractInfo): ProjectContractInfo
}
