package io.cloudflight.jems.api.project.contracting

import io.cloudflight.jems.api.project.dto.contracting.ProjectContractingManagementDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Contracting Management")
interface ContractingManagementApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_MANAGEMENT = "/api/project/{projectId}/contracting/management"
    }

    @ApiOperation("Get Project Contracting Management")
    @GetMapping(ENDPOINT_API_CONTRACTING_MANAGEMENT)
    fun getContractingManagement(@PathVariable projectId: Long): List<ProjectContractingManagementDTO>

    @ApiOperation("Update Project Contracting Management")
    @PutMapping(ENDPOINT_API_CONTRACTING_MANAGEMENT, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateContractingManagement(
        @PathVariable projectId: Long,
        @RequestBody projectManagers: List<ProjectContractingManagementDTO>
    ): List<ProjectContractingManagementDTO>

}
