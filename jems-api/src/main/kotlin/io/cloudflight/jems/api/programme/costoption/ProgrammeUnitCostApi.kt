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
import org.springframework.web.bind.annotation.RequestMapping

@Api("Programme Cost Option")
@RequestMapping("/api/costOption/unitCost")
interface ProgrammeUnitCostApi {

    @ApiOperation("Retrieve all programme unit costs")
    @GetMapping
    fun getProgrammeUnitCosts(): List<ProgrammeUnitCostListDTO>

    @ApiOperation("Retrieve programme unit cost by id")
    @GetMapping("/{unitCostId}")
    fun getProgrammeUnitCost(@PathVariable unitCostId: Long): ProgrammeUnitCostDTO

    @ApiOperation("Create programme unit cost")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProgrammeUnitCost(@RequestBody unitCost: ProgrammeUnitCostDTO): ProgrammeUnitCostDTO

    @ApiOperation("Update existing programme unit cost")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeUnitCost(@RequestBody unitCost: ProgrammeUnitCostDTO): ProgrammeUnitCostDTO

    @ApiOperation("Delete programme unit cost")
    @DeleteMapping("/{unitCostId}")
    fun deleteProgrammeUnitCost(@PathVariable unitCostId: Long)

}
