package io.cloudflight.jems.api.project.contracting.partner

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerBeneficialOwnerDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerDocumentsLocationDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Contracting Partner Location of Documents")
interface ContractingPartnerDocumentsLocationApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_PARTNER_DOCUMENTS_LOCATION =
            "/api/project/{projectId}/contracting/partner/documentsLocation/byPartnerId/{partnerId}"
    }

    @ApiOperation("Returns all location of documents for project partner")
    @GetMapping(ENDPOINT_API_CONTRACTING_PARTNER_DOCUMENTS_LOCATION)
    fun getDocumentsLocation(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
    ): ContractingPartnerDocumentsLocationDTO

    @ApiOperation("Updates location of documents of project partner")
    @PutMapping(ENDPOINT_API_CONTRACTING_PARTNER_DOCUMENTS_LOCATION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateDocumentsLocation(
        @PathVariable projectId: Long,
        @PathVariable partnerId: Long,
        @RequestBody documentsLocation: ContractingPartnerDocumentsLocationDTO,
    ): ContractingPartnerDocumentsLocationDTO
}
