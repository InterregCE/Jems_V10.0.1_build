package io.cloudflight.jems.api.common

import io.cloudflight.jems.api.common.dto.VersionDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Api("Info")
@RequestMapping("/api/_info")
interface InfoApi {

    @ApiOperation("Returns version info")
    @GetMapping("/version")
    fun getVersionInfo(): VersionDTO
}
