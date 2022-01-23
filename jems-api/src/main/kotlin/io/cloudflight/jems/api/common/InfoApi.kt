package io.cloudflight.jems.api.common

import io.cloudflight.jems.api.common.dto.VersionDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping

@Api("Info")
interface InfoApi {

    companion object {
        private const val ENDPOINT_API_INFO = "/api/_info"
    }

    @ApiOperation("Returns version info")
    @GetMapping("$ENDPOINT_API_INFO/version")
    fun getVersionInfo(): VersionDTO
}
