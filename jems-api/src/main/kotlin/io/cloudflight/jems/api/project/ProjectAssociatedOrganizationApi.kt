package io.cloudflight.jems.api.project

import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
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
import org.springframework.web.bind.annotation.RequestParam

@Api("Project Associated Organization")
interface ProjectAssociatedOrganizationApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_ASSOCIATED_ORGANIZATION = "/api/project/{projectId}/organization"
    }

    @ApiOperation("Returns all associated organization")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping(ENDPOINT_API_PROJECT_ASSOCIATED_ORGANIZATION)
    fun getAssociatedOrganizations(
        @PathVariable projectId: Long, pageable: Pageable, @RequestParam(required = false) version: String? = null
    ): Page<OutputProjectAssociatedOrganization>

    @ApiOperation("Returns an associated organization by id")
    @GetMapping("$ENDPOINT_API_PROJECT_ASSOCIATED_ORGANIZATION/{id}")
    fun getAssociatedOrganizationById(
        @PathVariable projectId: Long,
        @PathVariable id: Long,
        @RequestParam(required = false) version: String? = null
    ): OutputProjectAssociatedOrganizationDetail?

    @ApiOperation("Creates new associated organization")
    @PostMapping(ENDPOINT_API_PROJECT_ASSOCIATED_ORGANIZATION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createAssociatedOrganization(
        @PathVariable projectId: Long,
        @RequestBody associatedOrganization: InputProjectAssociatedOrganization
    ): OutputProjectAssociatedOrganizationDetail

    @ApiOperation("Update an associated organization")
    @PutMapping(ENDPOINT_API_PROJECT_ASSOCIATED_ORGANIZATION, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAssociatedOrganization(
        @PathVariable projectId: Long,
        @RequestBody associatedOrganization: InputProjectAssociatedOrganization
    ): OutputProjectAssociatedOrganizationDetail

    @ApiOperation("Delete an associated organization")
    @DeleteMapping("$ENDPOINT_API_PROJECT_ASSOCIATED_ORGANIZATION/{id}")
    fun deleteAssociatedOrganization(
        @PathVariable projectId: Long,
        @PathVariable id: Long
    )

    @ApiOperation("Deactivate an associated organization")
    @PutMapping("$ENDPOINT_API_PROJECT_ASSOCIATED_ORGANIZATION/{id}/deactivate")
    fun deactivateAssociatedOrganization(
        @PathVariable projectId: Long,
        @PathVariable id: Long
    )

}
