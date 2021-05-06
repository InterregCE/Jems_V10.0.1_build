package io.cloudflight.jems.api.plugin

import io.cloudflight.jems.api.plugin.dto.PluginInfoDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Api("Plugin")
@RequestMapping("/api/plugin")
interface PluginApi {

    @ApiOperation("list available plugins")
    @GetMapping("list")
    fun getAvailablePluginList(): List<PluginInfoDTO>
}
