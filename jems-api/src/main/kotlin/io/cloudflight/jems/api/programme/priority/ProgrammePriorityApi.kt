package io.cloudflight.jems.api.programme.priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityAvailableSetupDTO
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

@Api("Programme Priority")
@RequestMapping("/api/programmePriority")
interface ProgrammePriorityApi {

    @ApiOperation("Retrieve list of programme priorities")
    @GetMapping
    fun get(): List<ProgrammePriorityDTO>

    @ApiOperation("Retrieve programme priority by ID")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ProgrammePriorityDTO

    @ApiOperation("Creates new programme priority")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody priority: ProgrammePriorityDTO): ProgrammePriorityDTO

    @ApiOperation("Updates existing programme priority")
    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@PathVariable id: Long, @RequestBody priority: ProgrammePriorityDTO): ProgrammePriorityDTO

    @ApiOperation("Delete programme priority")
    @DeleteMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun delete(@PathVariable id: Long)

    @ApiOperation("Get free possibilities for priorities and objectives")
    @GetMapping("/availableSetup")
    fun getAvailableSetup(): ProgrammePriorityAvailableSetupDTO

}
