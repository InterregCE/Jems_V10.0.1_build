package io.cloudflight.jems.api.project.contracting.partner

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerBankingDetailsDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Contracting Partner Banking Details")
interface ContractingPartnerBankingDetailsApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_PARTNER_BANKING_DETAILS =
            "api/project/{projectId}/contracting/partner/bankingDetails/byPartnerId/{partnerId}"
    }

    @ApiOperation("Returns banking details for a project partner")
    @GetMapping(ENDPOINT_API_CONTRACTING_PARTNER_BANKING_DETAILS)
    fun getBankingDetails(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long
    ) : ContractingPartnerBankingDetailsDTO?

    @ApiOperation("Updates the banking details for a project partner")
    @PutMapping(ENDPOINT_API_CONTRACTING_PARTNER_BANKING_DETAILS, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBankingDetails(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @RequestBody bankingDetailsDTO: ContractingPartnerBankingDetailsDTO
    ) : ContractingPartnerBankingDetailsDTO?
}