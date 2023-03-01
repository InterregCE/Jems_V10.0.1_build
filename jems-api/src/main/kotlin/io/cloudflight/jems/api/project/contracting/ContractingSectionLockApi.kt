package io.cloudflight.jems.api.project.contracting

import io.cloudflight.jems.api.project.dto.contracting.ContractingSectionDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@Api("Contracting Section Lock")
interface ContractingSectionLockApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_SECTION_LOCK = "/api/project/{projectId}/contracting/sections/"
    }

    @ApiOperation("Get all locked sections")
    @GetMapping("${ENDPOINT_API_CONTRACTING_SECTION_LOCK}/locked")
    fun getLockedSections(@PathVariable projectId: Long): List<ContractingSectionDTO>


    @ApiOperation("Lock Contracting section")
    @GetMapping("${ENDPOINT_API_CONTRACTING_SECTION_LOCK}/{sectionName}/lock")
    fun lock(@PathVariable projectId: Long, @PathVariable sectionName: ContractingSectionDTO)


    @ApiOperation("Unlock Contracting section")
    @GetMapping("${ENDPOINT_API_CONTRACTING_SECTION_LOCK}/{sectionName}/unlock")
    fun unlock(@PathVariable projectId: Long, @PathVariable sectionName: ContractingSectionDTO)


}