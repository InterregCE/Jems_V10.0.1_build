package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationUpdate
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganizationDetail
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Api("Project Associated Organization")
@RequestMapping("/api/project/{projectId}/organization")
interface ProjectAssociatedOrganizationApi {
    @ApiOperation("Returns all associated organization")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping
    fun getAssociatedOrganizations(@PathVariable projectId: Long, pageable: Pageable): Page<OutputProjectAssociatedOrganization>

    @ApiOperation("Returns an associated organization by id")
    @GetMapping("/{id}")
    fun getAssociatedOrganizationById(@PathVariable projectId: Long,
                                      @PathVariable id: Long): OutputProjectAssociatedOrganizationDetail

    @ApiOperation("Creates new associated organization")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createAssociatedOrganization(@PathVariable projectId: Long,
                                     @Valid @RequestBody associatedOrganization: InputProjectAssociatedOrganizationCreate
    ): OutputProjectAssociatedOrganizationDetail

    @ApiOperation("Update an associated organization")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAssociatedOrganization(@PathVariable projectId: Long,
                                     @Valid @RequestBody associatedOrganization: InputProjectAssociatedOrganizationUpdate
    ): OutputProjectAssociatedOrganizationDetail

    @ApiOperation("Delete an associated organization")
    @DeleteMapping("/{id}")
    fun deleteAssociatedOrganization(@PathVariable projectId: Long,
                                     @PathVariable id: Long)
}
