package io.cloudflight.jems.api.plugin

import io.cloudflight.jems.api.plugin.dto.PluginInfoDTO
import io.cloudflight.jems.api.plugin.dto.PluginTypeDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Api("Plugin")
interface PluginApi {

    companion object {
        private const val ENDPOINT_API_PLUGIN = "/api/plugin"
    }

    @ApiOperation("list available plugins")
    @GetMapping("$ENDPOINT_API_PLUGIN/list/{type}")
    fun getAvailablePluginList(@PathVariable type: PluginTypeDTO): List<PluginInfoDTO>

    @ApiOperation("enable plugin")
    @PostMapping("$ENDPOINT_API_PLUGIN/{pluginKey}/enable")
    fun enablePlugin(@PathVariable pluginKey: String)

    @ApiOperation("disable plugin")
    @PostMapping("$ENDPOINT_API_PLUGIN/{pluginKey}/disable")
    fun disablePlugin(@PathVariable pluginKey: String)
}
