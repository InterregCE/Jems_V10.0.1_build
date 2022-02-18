package io.cloudflight.jems.api.common

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping

@Api("Settings")
interface SettingsApi {

    companion object {
        private const val ENDPOINT_API_SETTINGS = "/api/settings"
    }

    @ApiOperation("Get maximum allowed file size.")
    @GetMapping("$ENDPOINT_API_SETTINGS/maxFileSize")
    fun getMaximumAllowedFileSize() :  Int?
}
