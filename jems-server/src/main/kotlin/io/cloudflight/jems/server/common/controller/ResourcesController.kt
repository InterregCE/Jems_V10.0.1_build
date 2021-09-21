package io.cloudflight.jems.server.common.controller

import io.cloudflight.jems.api.common.ResourcesApi
import io.cloudflight.jems.api.common.dto.LogoDTO
import io.cloudflight.jems.server.resources.service.get_logos.GetLogos
import org.springframework.web.bind.annotation.RestController

@RestController
class ResourcesController(
    private val getLogos: GetLogos
) : ResourcesApi {

    override fun getLogos(): List<LogoDTO> = getLogos.getLogos()
}
