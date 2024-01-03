package io.cloudflight.jems.api.project.checklist

import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.checklist.ChecklistConsolidatorOptionsDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceSelectionDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@Api("Checklist Instance")
interface ChecklistInstanceApi {

    companion object {
        private const val ENDPOINT_API_CHECKLIST_INSTANCE = "/api/checklist/instance"
    }

    @ApiOperation("Retrieve the checklists instances for the current user related to specific id and checklist programme type")
    @GetMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/mine/{relatedToId}/{type}")
    fun getMyChecklistInstances(
        @PathVariable relatedToId: Long,
        @PathVariable type: ProgrammeChecklistTypeDTO
    ): List<ChecklistInstanceDTO>

    @ApiOperation("Retrieve all checklists instances related to specific id and checklist programme type")
    @GetMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/{relatedToId}/{type}")
    fun getAllChecklistInstances(
        @PathVariable relatedToId: Long,
        @PathVariable type: ProgrammeChecklistTypeDTO
    ): List<ChecklistInstanceDTO>

    @ApiOperation("Retrieve all checklists instances related to specific id object and programme type for selection")
    @GetMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/selection/{relatedToId}/{type}")
    fun getChecklistInstancesForSelection(
        @PathVariable relatedToId: Long,
        @PathVariable type: ProgrammeChecklistTypeDTO
    ): List<ChecklistInstanceSelectionDTO>

    @ApiOperation("Retrieve detailed checklist instance")
    @GetMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/{relatedToId}/detail/{checklistId}")
    fun getChecklistInstanceDetail(@PathVariable checklistId: Long, @PathVariable relatedToId: Long): ChecklistInstanceDetailDTO

    @ApiOperation("Create a checklist instance")
    @PostMapping(ENDPOINT_API_CHECKLIST_INSTANCE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createChecklistInstance(@RequestBody checklist: CreateChecklistInstanceDTO): ChecklistInstanceDetailDTO

    @ApiOperation("Clone a checklist instance")
    @PostMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/{checklistId}/clone")
    fun cloneChecklistInstance(@PathVariable checklistId: Long): ChecklistInstanceDetailDTO

    @ApiOperation("Update a checklist instance")
    @PutMapping(ENDPOINT_API_CHECKLIST_INSTANCE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateChecklistInstance(@RequestBody checklist: ChecklistInstanceDetailDTO): ChecklistInstanceDetailDTO

    @ApiOperation("Changes the status for a checklist instances")
    @PutMapping("${ENDPOINT_API_CHECKLIST_INSTANCE}/status/{checklistId}/{status}")
    fun changeChecklistStatus(@PathVariable checklistId: Long, @PathVariable status: ChecklistInstanceStatusDTO): ChecklistInstanceDTO

    @ApiOperation("Set the consolidated flag for a checklist instances")
    @PutMapping("${ENDPOINT_API_CHECKLIST_INSTANCE}/consolidate/{checklistId}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun consolidateChecklistInstance(@PathVariable checklistId: Long, @RequestBody options: ChecklistConsolidatorOptionsDTO): Boolean

    @ApiOperation("Update selection of checklist instances")
    @PutMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/selection", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateChecklistInstanceSelection(@RequestBody selection: Map<Long, Boolean>)

    @ApiOperation("Update description for checklist instance")
    @PutMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/description/{checklistId}", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun updateChecklistDescription(@PathVariable checklistId: Long, @RequestBody description: String?): ChecklistInstanceDTO

    @ApiOperation("Delete a checklist instance")
    @DeleteMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/{projectId}/{checklistId}")
    fun deleteChecklistInstance(@PathVariable checklistId: Long, @PathVariable projectId: Long)

    @ApiOperation("Export checklist instance")
    @GetMapping("$ENDPOINT_API_CHECKLIST_INSTANCE/export/{projectId}/{checklistId}")
    fun exportChecklistInstance(
        @PathVariable projectId: Long,
        @PathVariable checklistId: Long,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam(required = false) pluginKey: String?,
    ): ResponseEntity<ByteArrayResource>
}
