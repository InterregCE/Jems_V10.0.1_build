package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.InputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.InputProjectPartnerOrganizationDetails
import io.cloudflight.jems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.OutputProjectPartnerDetail
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.DeleteMapping
import javax.validation.Valid

@Api("Project Partner")
@RequestMapping("/api/project/{projectId}/partner")
interface ProjectPartnerApi {

    @ApiOperation("Returns all project partners")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping
    fun getProjectPartners(@PathVariable projectId: Long, pageable: Pageable): Page<OutputProjectPartner>

    @ApiOperation("Returns a project partner by id")
    @GetMapping("/{id}")
    fun getProjectPartnerById(@PathVariable projectId: Long,
                              @PathVariable id: Long): OutputProjectPartnerDetail

    @ApiOperation("Creates new project partner")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProjectPartner(@PathVariable projectId: Long,
                             @Valid @RequestBody projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail

    @ApiOperation("Update project partner")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartner(@PathVariable projectId: Long,
                             @Valid @RequestBody projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail

    @ApiOperation("Update project partner contact")
    @PutMapping("/{id}/contact", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnerContact(@PathVariable projectId: Long,
                                    @PathVariable id: Long,
                                    @Valid @RequestBody projectPartner: Set<InputProjectPartnerContact>): OutputProjectPartnerDetail

    @ApiOperation("Update project partner contribution")
    @PutMapping("/{id}/contribution", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnerContribution(@PathVariable projectId: Long,
                                    @PathVariable id: Long,
                                    @Valid @RequestBody partnerContribution: InputProjectPartnerContribution): OutputProjectPartnerDetail

    @ApiOperation("Update project partner organization details")
    @PutMapping("/{id}/details", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartnerOrganizationDetails(@PathVariable projectId: Long,
                                                @PathVariable id: Long,
                                                @Valid @RequestBody partnerOrganizationDetails: InputProjectPartnerOrganizationDetails
    ): OutputProjectPartnerDetail

    @ApiOperation("Delete a project partnjer")
    @DeleteMapping("/{id}")
    fun deleteProjectPartner(@PathVariable projectId: Long,
                             @PathVariable id: Long): Boolean

}
