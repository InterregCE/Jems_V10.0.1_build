package io.cloudflight.jems.api.project.contracting.partner

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerBeneficialOwnerDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Contracting Partner Beneficial Owner")
interface ContractingPartnerBeneficialOwnerApi {
    companion object {
        private const val ENDPOINT_API_CONTRACTING_PARTNER_BENEFICIAL_OWNER =
            "/api/project/{projectId}/contracting/partner/beneficialOwner/byPartnerId/{partnerId}"
    }

    @ApiOperation("Returns all beneficial owners for project partner")
    @GetMapping(ENDPOINT_API_CONTRACTING_PARTNER_BENEFICIAL_OWNER)
    fun getBeneficialOwners(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
    ): List<ContractingPartnerBeneficialOwnerDTO>

    @ApiOperation("Updates beneficial owners of project partner")
    @PutMapping(ENDPOINT_API_CONTRACTING_PARTNER_BENEFICIAL_OWNER, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateBeneficialOwners(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @RequestBody owners: List<ContractingPartnerBeneficialOwnerDTO>,
    ): List<ContractingPartnerBeneficialOwnerDTO>
}
