package io.cloudflight.jems.server.project.controller.contracting.contracts

import io.cloudflight.jems.api.project.contracting.ContractInfoApi
import io.cloudflight.jems.api.project.dto.contracting.ProjectContractInfoDTO
import io.cloudflight.jems.api.project.dto.contracting.ContractInfoUpdateDTO
import io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo.GetContractInfoInteractor
import io.cloudflight.jems.server.project.service.contracting.contractInfo.updateContractInfo.UpdateContractInfoInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractInfoController(
    private val getContractInfoInteractor: GetContractInfoInteractor,
    private val updateContractInfoInteractor: UpdateContractInfoInteractor,
): ContractInfoApi {

    override fun getProjectContractInfo(projectId: Long): ProjectContractInfoDTO =
        getContractInfoInteractor.getContractInfo(projectId).toDTO()


    override fun updateProjectContractInfo(projectId: Long, contractInfo: ContractInfoUpdateDTO): ContractInfoUpdateDTO =
        updateContractInfoInteractor.updateContractInfo(projectId, contractInfo.toModel()).toUpdateContractInfoDTO()

}
