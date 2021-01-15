package io.cloudflight.jems.api.project.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputUpdateDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid

@Api("WorkPackageOutput")
@RequestMapping("/api/project/workPackage/output/forWorkPackage/{workPackageId}")
interface ProjectWorkPackageOutputApi {

    @ApiOperation("Returns all work package outputs for a work package")
    @GetMapping
    fun getWorkPackageOutputs(@PathVariable workPackageId: Long): List<WorkPackageOutputDTO>

    @ApiOperation("Updates work packages outputs for a work package")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateWorkPackageOutputs(
        @PathVariable workPackageId: Long,
        @Valid @RequestBody workPackageOutputUpdateDTO: List<WorkPackageOutputUpdateDTO>
    ): List<WorkPackageOutputDTO>

}
