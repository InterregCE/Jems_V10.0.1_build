package io.cloudflight.jems.api.project.controlChecklist

import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceDetailDTO
import io.cloudflight.jems.api.project.dto.checklist.CreateChecklistInstanceDTO
import io.cloudflight.jems.api.project.dto.checklist.ChecklistInstanceStatusDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Control checklist Instance")
interface ControlChecklistInstanceApi {

    companion object {
        private const val ENDPOINT_API_CONTROL_CHECKLIST_INSTANCE = "/api/controlChecklist/byPartnerId/{partnerId}/byReportId/{reportId}"
    }

    @ApiOperation("Retrieve all control checklists instances for a specific control report")
    @GetMapping(ENDPOINT_API_CONTROL_CHECKLIST_INSTANCE)
    fun getAllControlChecklistInstances(@PathVariable partnerId: Long,
                                        @PathVariable reportId: Long): List<ChecklistInstanceDTO>

    @ApiOperation("Retrieve the details for a specific control checklist")
    @GetMapping("$ENDPOINT_API_CONTROL_CHECKLIST_INSTANCE/detail/{checklistId}")
    fun getControlChecklistInstanceDetail(@PathVariable partnerId: Long,
                                          @PathVariable reportId: Long,
                                          @PathVariable checklistId: Long): ChecklistInstanceDetailDTO

    @ApiOperation("Create a new control checklist instance")
    @PostMapping(ENDPOINT_API_CONTROL_CHECKLIST_INSTANCE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createControlChecklistInstance(@PathVariable partnerId: Long,
                                       @PathVariable reportId: Long,
                                       @RequestBody checklist: CreateChecklistInstanceDTO): ChecklistInstanceDetailDTO

    @ApiOperation("Update an existing control checklist instance")
    @PutMapping(ENDPOINT_API_CONTROL_CHECKLIST_INSTANCE, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateControlChecklistInstance(@PathVariable partnerId: Long,
                                       @PathVariable reportId: Long,
                                       @RequestBody checklist: ChecklistInstanceDetailDTO): ChecklistInstanceDetailDTO

    @ApiOperation("Changes the status for an existing control checklist instance")
    @PutMapping("${ENDPOINT_API_CONTROL_CHECKLIST_INSTANCE}/status/{checklistId}/{status}")
    fun changeControlChecklistStatus(@PathVariable partnerId: Long,
                                     @PathVariable reportId: Long,
                                     @PathVariable checklistId: Long,
                                     @PathVariable status: ChecklistInstanceStatusDTO): ChecklistInstanceDTO

    @ApiOperation("Delete an existing control checklist instance")
    @DeleteMapping("${ENDPOINT_API_CONTROL_CHECKLIST_INSTANCE}/delete/{checklistId}")
    fun deleteControlChecklistInstance(@PathVariable partnerId: Long,
                                       @PathVariable reportId: Long,
                                       @PathVariable checklistId: Long)
}