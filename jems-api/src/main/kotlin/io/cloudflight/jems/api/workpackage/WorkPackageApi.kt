package io.cloudflight.jems.api.workpackage;

import io.cloudflight.jems.api.workpackage.dto.InputWorkPackageCreate
import io.cloudflight.jems.api.workpackage.dto.InputWorkPackageUpdate
import io.cloudflight.jems.api.workpackage.dto.OutputWorkPackage
import io.cloudflight.jems.api.workpackage.dto.OutputWorkPackageSimple
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
@RequestMapping("/api/project/{projectId}/workpackage")
interface WorkPackageApi {

    @ApiOperation("Returns a work package by id")
    @GetMapping("/{id}")
    fun getWorkPackageById(@PathVariable projectId: Long, @PathVariable id: Long): OutputWorkPackage

    @ApiOperation("Returns all work packages for a project")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping
    fun getWorkPackagesByProjectId(@PathVariable projectId: Long, pageable: Pageable): Page<OutputWorkPackageSimple>

    @ApiOperation("Create work package")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createWorkPackage(@PathVariable projectId: Long, @Valid @RequestBody inputWorkPackageCreate: InputWorkPackageCreate): OutputWorkPackage

    @ApiOperation("Update work package")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPackage(@PathVariable projectId: Long, @Valid @RequestBody inputWorkPackageUpdate: InputWorkPackageUpdate): OutputWorkPackage

    @ApiOperation("Delete a work package")
    @DeleteMapping("/{id}")
    fun deleteWorkPackage(@PathVariable projectId: Long, @PathVariable id: Long)

}
