package io.cloudflight.jems.api.project.contracting

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
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

@Api("Contracting checklist Instance")
interface ContractingChecklistInstanceApi {

    companion object {
        private const val ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE =
            "/api/contractingChecklist/byProjectId/{projectId}"
    }

    @ApiOperation("Retrieve all contracting checklists instances for a specific project")
    @GetMapping(ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE)
    fun getAllContractingChecklistInstances(@PathVariable projectId: Long): List<ChecklistInstanceDTO>

    @ApiOperation("Retrieve the details for a specific contracting checklist")
    @GetMapping("$ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE/detail/{checklistId}")
    fun getContractingChecklistInstanceDetail(
        @PathVariable projectId: Long,
        @PathVariable checklistId: Long
    ): ChecklistInstanceDetailDTO

    @ApiOperation("Create a new contracting checklist instance")
    @PostMapping(ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createContractingChecklistInstance(
        @PathVariable projectId: Long,
        @RequestBody checklist: CreateChecklistInstanceDTO
    ): ChecklistInstanceDetailDTO

    @ApiOperation("Update an existing contracting checklist instance")
    @PutMapping(ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateContractingChecklistInstance(
        @PathVariable projectId: Long,
        @RequestBody checklist: ChecklistInstanceDetailDTO
    ): ChecklistInstanceDetailDTO

    @ApiOperation("Changes the status for an existing contracting checklist instance")
    @PutMapping("$ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE/status/{checklistId}/{status}")
    fun changeContractingChecklistStatus(
        @PathVariable projectId: Long,
        @PathVariable checklistId: Long,
        @PathVariable status: ChecklistInstanceStatusDTO
    ): ChecklistInstanceDTO

    @ApiOperation("Delete an existing contracting checklist instance")
    @DeleteMapping("$ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE/delete/{checklistId}")
    fun deleteContractingChecklistInstance(
        @PathVariable projectId: Long,
        @PathVariable checklistId: Long
    )

    @ApiOperation("Update description for checklist instance")
    @PutMapping("$ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE/description/{checklistId}", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun updateContractingChecklistDescription(
        @PathVariable projectId: Long,
        @PathVariable checklistId: Long,
        @RequestBody description: String?
    ): ChecklistInstanceDTO

    @ApiOperation("Export contracting checklist instance")
    @GetMapping("$ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE/export/{checklistId}")
    fun exportContractingChecklistInstance(
        @PathVariable projectId: Long,
        @PathVariable checklistId: Long,
        @RequestParam exportLanguage: SystemLanguage,
        @RequestParam(required = false) pluginKey: String?,
    ): ResponseEntity<ByteArrayResource>

    @ApiOperation("Clone contracting checklist instance")
    @PostMapping("$ENDPOINT_API_CONTRACTING_CHECKLIST_INSTANCE/clone/{checklistId}")
    fun cloneContractingChecklistInstance(
        @PathVariable projectId: Long,
        @PathVariable checklistId: Long
    ): ChecklistInstanceDetailDTO
}
