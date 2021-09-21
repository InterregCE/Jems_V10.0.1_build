package io.cloudflight.jems.server.resources.repository

import io.cloudflight.jems.api.common.dto.LogoDTO

interface GetLogosPersistence {
    fun getLogos(): List<LogoDTO>
}
