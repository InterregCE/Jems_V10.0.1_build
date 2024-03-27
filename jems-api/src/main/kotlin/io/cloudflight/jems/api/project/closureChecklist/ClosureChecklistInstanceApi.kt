package io.cloudflight.jems.api.project.closureChecklist

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Api("Closure checklist Instance")
interface ClosureChecklistInstanceApi {

    companion object {
        private const val ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE =
            "/api/closureChecklist/byProjectId/{projectId}/byReportId/{reportId}"
    }

    @ApiOperation("Retrieve all closure checklists instances for a specific project report")
    @GetMapping(ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE)
    fun getAllClosureChecklistInstances(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long
    ): List<ChecklistInstanceDTO>

    @ApiOperation("Retrieve the details for a specific closure checklist")
    @GetMapping("${ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE}/detail/{checklistId}")
    fun getClosureChecklistInstanceDetail(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable checklistId: Long
    ): ChecklistInstanceDetailDTO

    @ApiOperation("Create a new closure checklist instance")
    @PutMapping("${ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE}/byProgrammeChecklist/{programmeChecklistId}")
    fun createClosureChecklistInstance(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable programmeChecklistId: Long
    ): ChecklistInstanceDetailDTO

    @ApiOperation("Update an existing closure checklist instance")
    @PutMapping(ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateClosureChecklistInstance(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @RequestBody checklist: ChecklistInstanceDetailDTO
    ): ChecklistInstanceDetailDTO

    @ApiOperation("Changes the status for an existing closure checklist instance")
    @PutMapping("${ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE}/status/{checklistId}/{status}")
    fun changeClosureChecklistStatus(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable checklistId: Long,
        @PathVariable status: ChecklistInstanceStatusDTO
    ): ChecklistInstanceDTO

    @ApiOperation("Delete an existing closure checklist instance")
    @DeleteMapping("${ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE}/delete/{checklistId}")
    fun deleteClosureChecklistInstance(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable checklistId: Long
    )

    @ApiOperation("Update description for closure checklist instance")
    @PutMapping("${ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE}/description/{checklistId}", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun updateClosureChecklistDescription(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable checklistId: Long,
        @RequestBody description: String?
    ): ChecklistInstanceDTO

    @ApiOperation("Export closure checklist instance")
    @GetMapping("${ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE}/export/{checklistId}")
    fun exportClosureChecklistInstance(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable checklistId: Long,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam(required = false) pluginKey: String?,
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Clone closure checklist instance")
    @PutMapping("${ENDPOINT_API_CLOSURE_CHECKLIST_INSTANCE}/clone/{checklistId}")
    fun cloneClosureChecklistInstance(
        @PathVariable projectId: Long,
        @PathVariable reportId: Long,
        @PathVariable checklistId: Long
    ): ChecklistInstanceDetailDTO
}
