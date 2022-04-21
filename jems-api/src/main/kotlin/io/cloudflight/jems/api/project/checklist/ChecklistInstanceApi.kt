package io.cloudflight.jems.api.project.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Checklist Instance")
interface ChecklistInstanceApi {

    companion object {
        private const val ENDPOINT_API_CHECKLIST_INSTANCE = "/api/checklist/instance"
    }

    @ApiOperation("Retrieve all checklists instances related to specific id object (project) and checklist programme type")
    @GetMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/{relatedToId}/{type}")
    fun getChecklistInstances(
        @PathVariable relatedToId: Long,
        @PathVariable type: ProgrammeChecklistTypeDTO
    ): List<ChecklistInstanceDTO>

    @ApiOperation("Retrieve detailed checklist instance")
    @GetMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/detail/{checklistId}")
    fun getChecklistInstanceDetail(@PathVariable checklistId: Long): ChecklistInstanceDetailDTO

    @ApiOperation("Create a checklist instance")
    @PostMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/create", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createChecklistInstance(@RequestBody checklist: CreateChecklistInstanceDTO): ChecklistInstanceDetailDTO

    @ApiOperation("Update a checklist instance")
    @PutMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/update", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateChecklistInstance(@RequestBody checklist: ChecklistInstanceDetailDTO): ChecklistInstanceDetailDTO

    @ApiOperation("Delete a checklist instance")
    @DeleteMapping("${ENDPOINT_API_CHECKLIST_INSTANCE}/delete/{checklistId}")
    fun deleteChecklistInstance(@PathVariable checklistId: Long)
}
