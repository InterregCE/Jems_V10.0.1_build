package io.cloudflight.jems.api.plugin

import io.cloudflight.jems.api.plugin.dto.PluginInfoDTO
import io.cloudflight.jems.api.plugin.dto.PluginTypeDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Api("Plugin")
@RequestMapping("/api/plugin")
interface PluginApi {

    @ApiOperation("list available plugins")
    @GetMapping("list/{type}")
    fun getAvailablePluginList(@PathVariable type: PluginTypeDTO): List<PluginInfoDTO>

    @ApiOperation("enable plugin")
    @PostMapping("{pluginKey}/enable")
    fun enablePlugin(@PathVariable pluginKey: String)

    @ApiOperation("disable plugin")
    @PostMapping("{pluginKey}/disable")
    fun disablePlugin(@PathVariable pluginKey: String)
}
