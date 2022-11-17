package io.cloudflight.jems.server.project.service.contracting.contractInfo.updateContractInfo

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo

interface UpdateContractInfoInteractor {

    fun updateContractInfo(projectId: Long, contractInfo: ProjectContractInfo): ProjectContractInfo
}
