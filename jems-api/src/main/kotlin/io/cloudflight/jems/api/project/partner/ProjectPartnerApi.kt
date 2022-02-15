package io.cloudflight.jems.api.project.partner

import io.cloudflight.jems.api.project.dto.ProjectContactDTO
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.ProjectPartnerStateAidDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectBudgetPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerDetailDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.DeleteMapping
import javax.validation.Valid
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Partner")
interface ProjectPartnerApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_PARTNER = "/api/project/partner"
    }

    @ApiOperation("Returns all project partners")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER/byProjectId/{projectId}")
    fun getProjectPartners(
        @PathVariable projectId: Long,
        pageable: Pageable,
        @RequestParam(required = false) version: String? = null
    ): Page<ProjectBudgetPartnerSummaryDTO>

    @ApiOperation("Returns all project partners (only name)")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER/byProjectId/{projectId}/ids")
    fun getProjectPartnersForDropdown(@PathVariable projectId: Long,
                                      pageable: Pageable,
                                      @RequestParam(required = false) version: String? = null): List<ProjectPartnerSummaryDTO>

    @ApiOperation("Returns a project partner by id")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER/{partnerId}")
    fun getProjectPartnerById(
        @PathVariable partnerId: Long,
        @RequestParam(required = false) version: String? = null
    ): ProjectPartnerDetailDTO

    @ApiOperation("Creates new project partner")
    @PostMapping("$ENDPOINT_API_PROJECT_PARTNER/toProjectId/{projectId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProjectPartner(
        @PathVariable projectId: Long,
        @RequestBody projectPartner: ProjectPartnerDTO
    ): ProjectPartnerDetailDTO

    @ApiOperation("Update project partner")
    @PutMapping(ENDPOINT_API_PROJECT_PARTNER, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartner(
        @RequestBody projectPartner: ProjectPartnerDTO
    ): ProjectPartnerDetailDTO

    @ApiOperation("Update project partner addresses")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER/{partnerId}/address", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnerAddress(
        @PathVariable partnerId: Long,
        @Valid @RequestBody addresses: Set<ProjectPartnerAddressDTO>
    ): ProjectPartnerDetailDTO

    @ApiOperation("Update project partner contact")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER/{partnerId}/contact", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnerContact(
        @PathVariable partnerId: Long,
        @Valid @RequestBody contacts: Set<ProjectContactDTO>
    ): ProjectPartnerDetailDTO

    @ApiOperation("Update project partner motivation")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER/{partnerId}/motivation", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnerMotivation(
        @PathVariable partnerId: Long,
        @RequestBody motivation: ProjectPartnerMotivationDTO
    ): ProjectPartnerDetailDTO

    @ApiOperation("Get project partner state aid")
    @GetMapping("$ENDPOINT_API_PROJECT_PARTNER/{partnerId}/stateAid")
    fun getProjectPartnerStateAid(
        @PathVariable partnerId: Long,
        @RequestParam(required = false) version: String? = null
    ): ProjectPartnerStateAidDTO

    @ApiOperation("Update project partner state aid")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER/{partnerId}/stateAid", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnerStateAid(
        @PathVariable partnerId: Long,
        @RequestBody stateAid: ProjectPartnerStateAidDTO
    ): ProjectPartnerStateAidDTO

    @ApiOperation("Delete a project partner")
    @DeleteMapping("$ENDPOINT_API_PROJECT_PARTNER/{partnerId}")
    fun deleteProjectPartner(@PathVariable partnerId: Long)

    @ApiOperation("Deactivate a project partner")
    @PutMapping("$ENDPOINT_API_PROJECT_PARTNER/{partnerId}/deactivate")
    fun deactivate(@PathVariable partnerId: Long)

}
