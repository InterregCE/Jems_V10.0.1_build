package io.cloudflight.jems.api.project.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid
import org.springframework.web.bind.annotation.RequestParam

@Api("WorkPackageOutput")
@RequestMapping("/api/project/workPackage/output/forWorkPackage/{workPackageId}")
interface ProjectWorkPackageOutputApi {

    @ApiOperation("Returns all work package outputs for a work package")
    @GetMapping
    fun getOutputs(@PathVariable workPackageId: Long, @RequestParam projectId: Long, @RequestParam(required = false) version: String? = null): List<WorkPackageOutputDTO>

    @ApiOperation("Updates work packages outputs for a work package")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateOutputs(
        @PathVariable workPackageId: Long,
        @Valid @RequestBody outputs: List<WorkPackageOutputDTO>
    ): List<WorkPackageOutputDTO>

}
