package io.cloudflight.jems.api.project.workpackage;

import io.cloudflight.jems.api.project.dto.workpackage.activity.WorkPackageActivityDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("WorkPackageActivity")
@RequestMapping("/api/project/workPackage/activity/forWorkPackage/{workPackageId}")
interface ProjectWorkPackageActivityApi {

    @ApiOperation("Returns all work package activities for a work package")
    @GetMapping
    fun getActivities(@PathVariable workPackageId: Long): List<WorkPackageActivityDTO>

    @ApiOperation("Updates activities for a work package")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateActivities(
        @PathVariable workPackageId: Long,
        @RequestBody activities: List<WorkPackageActivityDTO>
    ): List<WorkPackageActivityDTO>

}
