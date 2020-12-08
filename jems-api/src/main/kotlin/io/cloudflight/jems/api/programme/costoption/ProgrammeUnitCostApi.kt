package io.cloudflight.jems.api.programme.costoption

import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping
    fun getProgrammeUnitCosts(pageable: Pageable): Page<ProgrammeUnitCostDTO>

    @ApiOperation("Create programme unit cost")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createProgrammeUnitCost(@RequestBody unitCost: ProgrammeUnitCostDTO): ProgrammeUnitCostDTO

    @ApiOperation("Update existing programme unit cost")
    @PutMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateProgrammeUnitCost(@RequestBody unitCost: ProgrammeUnitCostDTO): ProgrammeUnitCostDTO

    @ApiOperation("Delete programme unit cost")
    @DeleteMapping("/{unitCostId}")
    fun deleteProgrammeUnitCost(@PathVariable unitCostId: Long)

    @ApiOperation("Retrieve programme unit cost by id")
    @GetMapping("/unitCost/{id}")
    fun getProgrammeUnitCost(@PathVariable id: Long): ProgrammeUnitCostDTO

}
