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

@Api("Programme Priority")
interface ProgrammePriorityApi {

    companion object {
        private const val ENDPOINT_API_PROGRAMME_PRIORITY = "/api/programmePriority"
    }

    @ApiOperation("Retrieve list of programme priorities")
    @GetMapping(ENDPOINT_API_PROGRAMME_PRIORITY)
    fun get(): List<ProgrammePriorityDTO>

    @ApiOperation("Retrieve programme priority by ID")
    @GetMapping("$ENDPOINT_API_PROGRAMME_PRIORITY/{id}")
    fun getById(@PathVariable id: Long): ProgrammePriorityDTO

    @ApiOperation("Creates new programme priority")
    @PostMapping(ENDPOINT_API_PROGRAMME_PRIORITY, consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody priority: ProgrammePriorityDTO): ProgrammePriorityDTO

    @ApiOperation("Updates existing programme priority")
    @PutMapping("$ENDPOINT_API_PROGRAMME_PRIORITY/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@PathVariable id: Long, @RequestBody priority: ProgrammePriorityDTO): ProgrammePriorityDTO

    @ApiOperation("Delete programme priority")
    @DeleteMapping("$ENDPOINT_API_PROGRAMME_PRIORITY/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun delete(@PathVariable id: Long)

    @ApiOperation("Get free possibilities for priorities and objectives")
    @GetMapping("$ENDPOINT_API_PROGRAMME_PRIORITY/availableSetup")
    fun getAvailableSetup(): ProgrammePriorityAvailableSetupDTO

}
