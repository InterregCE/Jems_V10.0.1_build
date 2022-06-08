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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Api("WorkPackage")
interface ProjectWorkPackageApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_WORK_PACKAGE = "/api/project/{projectId}/workPackage"
    }

    @ApiOperation("Returns a work package by id")
    @GetMapping("$ENDPOINT_API_PROJECT_WORK_PACKAGE/{workPackageId}")
    fun getWorkPackageById(
        @PathVariable projectId: Long,
        @PathVariable workPackageId: Long,
        @RequestParam(required = false) version: String? = null
    ): OutputWorkPackage

    @ApiOperation("Returns all work packages for a project")
    @GetMapping(ENDPOINT_API_PROJECT_WORK_PACKAGE)
    fun getWorkPackagesByProjectId(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<OutputWorkPackageSimple>

    @ApiOperation("Returns all work packages for a project including outputs and activities")
    @GetMapping("$ENDPOINT_API_PROJECT_WORK_PACKAGE/withOutputsAndActivities")
    fun getWorkPackagesForTimePlanByProjectId(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null
    ): List<ProjectWorkPackageDTO>

    @ApiOperation("Create work package")
    @PostMapping(ENDPOINT_API_PROJECT_WORK_PACKAGE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createWorkPackage(
        @PathVariable projectId: Long,
        @RequestBody inputWorkPackageCreate: InputWorkPackageCreate
    ): OutputWorkPackage

    @ApiOperation("Update work package")
    @PutMapping(ENDPOINT_API_PROJECT_WORK_PACKAGE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPackage(
        @PathVariable projectId: Long,
        @RequestBody inputWorkPackageUpdate: InputWorkPackageUpdate
    ): OutputWorkPackage

    @ApiOperation("Delete a work package")
    @DeleteMapping("$ENDPOINT_API_PROJECT_WORK_PACKAGE/{workPackageId}")
    fun deleteWorkPackage(@PathVariable projectId: Long, @PathVariable workPackageId: Long)

}
