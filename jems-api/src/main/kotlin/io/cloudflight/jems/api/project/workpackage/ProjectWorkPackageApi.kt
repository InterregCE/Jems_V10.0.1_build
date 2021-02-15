package io.cloudflight.jems.api.project.workpackage;

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.api.project.dto.workpackage.ProjectWorkPackageDTO
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid

@Api("WorkPackage")
@RequestMapping("/api/project/workPackage")
interface ProjectWorkPackageApi {

    @ApiOperation("Returns a work package by id")
    @GetMapping("/{workPackageId}")
    fun getWorkPackageById(@PathVariable workPackageId: Long): OutputWorkPackage

    @ApiOperation("Returns all work packages for a project")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("/perProject/{projectId}")
    fun getWorkPackagesByProjectId(@PathVariable projectId: Long, pageable: Pageable): Page<OutputWorkPackageSimple>

    @ApiOperation("Returns all work packages for a project including activities")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("/perProject/{projectId}/full")
    fun getFullWorkPackagesByProjectId(@PathVariable projectId: Long, pageable: Pageable): Page<ProjectWorkPackageDTO>

    @ApiOperation("Create work package")
    @PostMapping("/forProject/{projectId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createWorkPackage(@PathVariable projectId: Long, @Valid @RequestBody inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage

    @ApiOperation("Update work package")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPackage(@Valid @RequestBody inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage

    @ApiOperation("Delete a work package")
    @DeleteMapping("/{workPackageId}")
    fun deleteWorkPackage(@PathVariable workPackageId: Long)

}
