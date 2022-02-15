package io.cloudflight.jems.api.common

import io.cloudflight.jems.api.common.dto.LogoDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping

@Api("Resources")
interface ResourcesApi {

    companion object {
        private const val ENDPOINT_API_RESOURCES = "/api/resources"
    }

    @ApiOperation("Returns logo")
    @GetMapping("$ENDPOINT_API_RESOURCES/logo")
    fun getLogos(): List<LogoDTO>
}
