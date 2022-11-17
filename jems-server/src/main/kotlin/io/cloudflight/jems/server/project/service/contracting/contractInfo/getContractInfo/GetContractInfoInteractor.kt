package io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo

interface GetContractInfoInteractor {

    fun getContractInfo(projectId: Long): ProjectContractInfo
}
