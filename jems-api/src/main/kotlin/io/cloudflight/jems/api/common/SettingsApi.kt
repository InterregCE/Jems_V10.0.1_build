package io.cloudflight.jems.api.common

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Api("Settings")
@RequestMapping("/api/settings")
interface SettingsApi {

    @ApiOperation("Get maximum allowed file size.")
    @GetMapping("/maxFileSize")
    fun getMaximumAllowedFileSize() :  Int?
}
