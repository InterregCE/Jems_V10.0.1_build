package io.cloudflight.jems.api.project.workpackage;

import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageCreate
import io.cloudflight.jems.api.project.dto.workpackage.InputWorkPackageUpdate
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.api.project.dto.workpackage.ProjectWorkPackageDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid
import org.springframework.web.bind.annotation.RequestParam

@Api("WorkPackage")
@RequestMapping("/api/project/workPackage")
interface ProjectWorkPackageApi {

    @ApiOperation("Returns a work package by id")
    @GetMapping("/{workPackageId}")
    fun getWorkPackageById(@PathVariable workPackageId: Long, @RequestParam(required = false) version: String? = null): OutputWorkPackage

    @ApiOperation("Returns all work packages for a project")
    @GetMapping("/perProject/{projectId}")
    fun getWorkPackagesByProjectId(@PathVariable projectId: Long, @RequestParam(required = false) version: String? = null): List<OutputWorkPackageSimple>

    @ApiOperation("Returns all work packages for a project including outputs and activities")
    @GetMapping("/perProject/{projectId}/withOutputsAndActivities")
    fun getWorkPackagesForTimePlanByProjectId(@PathVariable projectId: Long): List<ProjectWorkPackageDTO>

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
