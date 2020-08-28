package io.cloudflight.ems.api.project

import io.cloudflight.ems.api.project.dto.InputProjectPartnerCreate
import io.cloudflight.ems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.ems.api.project.dto.OutputProjectPartner
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
import javax.validation.Valid

@Api("Project Partner")
@RequestMapping("/api/project/{projectId}/partner")
interface ProjectPartnerApi {

    @ApiOperation("Returns all project partners")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping
    fun getProjectPartners(@PathVariable projectId: Long, pageable: Pageable): Page<OutputProjectPartner>

    @ApiOperation("Returns a project partner by id")
    @GetMapping("/{id}")
    fun getProjectPartnerById(@PathVariable projectId: Long,
                              @PathVariable id: Long): OutputProjectPartner

    @ApiOperation("Creates new project partner")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProjectPartner(@PathVariable projectId: Long,
                             @Valid @RequestBody projectPartner: InputProjectPartnerCreate): OutputProjectPartner

    @ApiOperation("Update project partner")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectPartner(@PathVariable projectId: Long,
                             @Valid @RequestBody projectPartner: InputProjectPartnerUpdate): OutputProjectPartner

}
