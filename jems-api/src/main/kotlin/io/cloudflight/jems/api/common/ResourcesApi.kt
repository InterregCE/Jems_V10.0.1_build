package io.cloudflight.jems.api.common

import io.cloudflight.jems.api.common.dto.LogoDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Api("Resources")
@RequestMapping("/api/resources")
interface ResourcesApi {

    @ApiOperation("Returns logo")
    @GetMapping("/logo")
    fun getLogos(): List<LogoDTO>
}
