package io.cloudflight.jems.api.project.contracting.partner

import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnersApi.Companion.ENDPOINT_API_CONTRACTING_PARTNERS
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Api("Project Contracting Partner Lock")
interface ContractingPartnerLockApi {

    @ApiOperation("Get locked contracting partners for project")
    @GetMapping("${ENDPOINT_API_CONTRACTING_PARTNERS}/locked")
    fun getLockedPartners(@PathVariable projectId: Long): Set<Long>

    @ApiOperation("Lock Contracting partner")
    @GetMapping("${ENDPOINT_API_CONTRACTING_PARTNERS}/{partnerId}/lock")
    fun lock(@PathVariable partnerId: Long, @PathVariable projectId: Long)


    @ApiOperation("Unlock Contracting partner")
    @GetMapping("${ENDPOINT_API_CONTRACTING_PARTNERS}/{partnerId}/unlock")
    fun unlock(@PathVariable partnerId: Long, @PathVariable projectId: Long)
}