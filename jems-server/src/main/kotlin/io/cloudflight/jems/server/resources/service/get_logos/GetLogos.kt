package io.cloudflight.jems.server.resources.service.get_logos

import io.cloudflight.jems.api.common.dto.LogoDTO

interface GetLogos {
    fun getLogos(): List<LogoDTO>
}
