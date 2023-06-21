package io.cloudflight.jems.api.project.contracting.partner

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidDeMinimisSectionDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidGberSectionDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidDeMinimisDTO
import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerStateAidGberDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Project Contracting Partner State Aid")
interface ContractingPartnerStateAidApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_PARTNER_STATE_AID =
            "/api/project/contracting/partner/stateAid/byPartnerId/{partnerId}"
    }

    @ApiOperation("Get State Aid de minimis section")
    @GetMapping("${ENDPOINT_API_CONTRACTING_PARTNER_STATE_AID}/minimis")
    fun getDeMinimisSection(
        @PathVariable partnerId: Long
    ): ContractingPartnerStateAidDeMinimisSectionDTO?

    @ApiOperation("Update State Aid de minimis section")
    @PutMapping("${ENDPOINT_API_CONTRACTING_PARTNER_STATE_AID}/minimis", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateDeMinimisSection(
        @PathVariable partnerId: Long,
        @RequestBody  deMinimisData: ContractingPartnerStateAidDeMinimisDTO
    ): ContractingPartnerStateAidDeMinimisSectionDTO

    @ApiOperation("Get State Aid GBER section")
    @GetMapping("${ENDPOINT_API_CONTRACTING_PARTNER_STATE_AID}/gber")
    fun getGberSection(
        @PathVariable partnerId: Long
    ): ContractingPartnerStateAidGberSectionDTO?

    @ApiOperation("Update State Aid GBER section")
    @PutMapping("${ENDPOINT_API_CONTRACTING_PARTNER_STATE_AID}/gber", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateGberSection(
        @PathVariable partnerId: Long,
        @RequestBody  gberData: ContractingPartnerStateAidGberDTO
    ): ContractingPartnerStateAidGberSectionDTO

}
