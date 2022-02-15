package io.cloudflight.jems.api.programme.costoption

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

@Api("Programme Cost Option")
interface ProgrammeUnitCostApi {

    companion object {
        private const val ENDPOINT_API_PROGRAMME_UNIT_COST = "/api/costOption/unitCost"
    }

    @ApiOperation("Retrieve all programme unit costs")
    @GetMapping(ENDPOINT_API_PROGRAMME_UNIT_COST)
    fun getProgrammeUnitCosts(): List<ProgrammeUnitCostListDTO>

    @ApiOperation("Retrieve programme unit cost by id")
    @GetMapping("$ENDPOINT_API_PROGRAMME_UNIT_COST/{unitCostId}")
    fun getProgrammeUnitCost(@PathVariable unitCostId: Long): ProgrammeUnitCostDTO

    @ApiOperation("Create programme unit cost")
    @PostMapping(ENDPOINT_API_PROGRAMME_UNIT_COST, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProgrammeUnitCost(@RequestBody unitCost: ProgrammeUnitCostDTO): ProgrammeUnitCostDTO

    @ApiOperation("Update existing programme unit cost")
    @PutMapping(ENDPOINT_API_PROGRAMME_UNIT_COST, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeUnitCost(@RequestBody unitCost: ProgrammeUnitCostDTO): ProgrammeUnitCostDTO

    @ApiOperation("Delete programme unit cost")
    @DeleteMapping("$ENDPOINT_API_PROGRAMME_UNIT_COST/{unitCostId}")
    fun deleteProgrammeUnitCost(@PathVariable unitCostId: Long)

}
