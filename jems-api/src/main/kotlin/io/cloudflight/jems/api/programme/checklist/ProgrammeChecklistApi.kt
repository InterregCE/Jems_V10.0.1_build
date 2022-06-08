package io.cloudflight.jems.api.programme.checklist

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistDetailDTO
import io.cloudflight.jems.api.programme.dto.checklist.ProgrammeChecklistTypeDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Api("Programme Checklist")
interface ProgrammeChecklistApi {

    companion object {
        private const val ENDPOINT_API_PROGRAMME_CHECKLIST = "/api/programme/checklist"
    }

    @ApiOperation("Retrieve all programme checklists")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", allowMultiple = true)
    )
    @GetMapping(ENDPOINT_API_PROGRAMME_CHECKLIST)
    fun getProgrammeChecklists(pageable: Pageable): List<ProgrammeChecklistDTO>

    @ApiOperation("Retrieve detailed programme checklist")
    @GetMapping("$ENDPOINT_API_PROGRAMME_CHECKLIST/detail/{checklistId}")
    fun getProgrammeChecklistDetail(@PathVariable checklistId: Long): ProgrammeChecklistDetailDTO

    @ApiOperation("Create a programme checklist")
    @PostMapping("$ENDPOINT_API_PROGRAMME_CHECKLIST/create", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProgrammeChecklist(@RequestBody checklist: ProgrammeChecklistDetailDTO): ProgrammeChecklistDetailDTO

    @ApiOperation("Update a programme checklist")
    @PutMapping("$ENDPOINT_API_PROGRAMME_CHECKLIST/update", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeChecklist(@RequestBody checklist: ProgrammeChecklistDetailDTO): ProgrammeChecklistDetailDTO

    @ApiOperation("Delete a programme checklist")
    @DeleteMapping("${ENDPOINT_API_PROGRAMME_CHECKLIST}/delete/{checklistId}")
    fun deleteChecklist(@PathVariable checklistId: Long)

    @ApiOperation("Retrieve programme checklists by type")
    @GetMapping("$ENDPOINT_API_PROGRAMME_CHECKLIST/byType/{checklistType}")
    fun getProgrammeChecklistsByType(@PathVariable checklistType: ProgrammeChecklistTypeDTO): List<IdNamePairDTO>
}
