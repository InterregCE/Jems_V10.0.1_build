package io.cloudflight.jems.api.project.budget

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostListDTO
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

@Api("Project Cost Option")
interface ProjectCostOptionApi {

    companion object {
        private const val ENDPOINT_API_PROJECT_COST_OPTION = "/api/project/{projectId}/costOption"
        private const val ENDPOINT_API_PROJECT_UNIT_COST = "$ENDPOINT_API_PROJECT_COST_OPTION/unitCost"
    }

    @ApiOperation("Get project available unit costs")
    @GetMapping("$ENDPOINT_API_PROJECT_UNIT_COST/available")
    fun getProjectAvailableUnitCosts(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null,
    ): List<ProgrammeUnitCostDTO>

    @ApiOperation("Retrieve all programme unit costs")
    @GetMapping(ENDPOINT_API_PROJECT_UNIT_COST)
    fun getProjectUnitCostList(
        @PathVariable projectId: Long,
        @RequestParam(required = false) version: String? = null,
    ): List<ProgrammeUnitCostListDTO>

    @ApiOperation("Retrieve project-proposed unit cost by id")
    @GetMapping("$ENDPOINT_API_PROJECT_UNIT_COST/{unitCostId}")
    fun getProjectUnitCost(
        @PathVariable projectId: Long,
        @PathVariable unitCostId: Long,
        @RequestParam(required = false) version: String? = null,
    ): ProgrammeUnitCostDTO

    @ApiOperation("Create project-proposed unit cost")
    @PostMapping(ENDPOINT_API_PROJECT_UNIT_COST, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProjectUnitCost(
        @PathVariable projectId: Long,
        @RequestBody unitCost: ProgrammeUnitCostDTO,
    ): ProgrammeUnitCostDTO

    @ApiOperation("Update existing project-proposed unit cost")
    @PutMapping(ENDPOINT_API_PROJECT_UNIT_COST, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProjectUnitCost(
        @PathVariable projectId: Long,
        @RequestBody unitCost: ProgrammeUnitCostDTO,
    ): ProgrammeUnitCostDTO

    @ApiOperation("Delete project-proposed unit cost")
    @DeleteMapping("$ENDPOINT_API_PROJECT_UNIT_COST/{unitCostId}")
    fun deleteProgrammeUnitCost(
        @PathVariable projectId: Long,
        @PathVariable unitCostId: Long,
    )

}
