package io.cloudflight.jems.api.project.contracting

import io.cloudflight.jems.api.project.dto.contracting.ProjectContractInfoDTO
import io.cloudflight.jems.api.project.dto.contracting.ContractInfoUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Contracts")
interface ContractInfoApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTS = "/api/project/{projectId}/contracting/contract"
    }

    @ApiOperation("Get Project Contract Section")
    @GetMapping(ENDPOINT_API_CONTRACTS)
    fun getProjectContractInfo(@PathVariable projectId: Long): ProjectContractInfoDTO

    @ApiOperation("Update Project Contract Section")
    @PutMapping(ENDPOINT_API_CONTRACTS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectContractInfo(
        @PathVariable projectId: Long,
        @RequestBody contractInfo: ContractInfoUpdateDTO
    ): ContractInfoUpdateDTO
}
