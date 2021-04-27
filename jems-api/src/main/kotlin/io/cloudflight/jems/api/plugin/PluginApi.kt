package io.cloudflight.jems.api.plugin

import io.cloudflight.jems.api.plugin.dto.PluginInfoDTO
import io.cloudflight.jems.api.plugin.dto.PreConditionCheckResultDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Api("Plugin")
@RequestMapping("/api/plugin")
interface PluginApi {

    @ApiOperation("list available plugins")
    @GetMapping("list")
    fun getAvailablePluginList(): List<PluginInfoDTO>

    //todo this is for demo only (should be removed with next release for sprint 25)
    @ApiOperation("demo precheck condition plugin execution")
    @GetMapping("preCheck/{projectId}")
    fun execute(@PathVariable projectId: Long): PreConditionCheckResultDTO
}
